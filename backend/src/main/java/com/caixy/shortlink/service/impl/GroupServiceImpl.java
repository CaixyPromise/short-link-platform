package com.caixy.shortlink.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.manager.authorization.AuthManager;
import com.caixy.shortlink.mapper.GroupMapper;
import com.caixy.shortlink.mapper.LinkMapper;
import com.caixy.shortlink.model.convertor.group.GroupConvertor;
import com.caixy.shortlink.model.dto.group.GroupAddRequest;
import com.caixy.shortlink.model.dto.group.GroupQueryRequest;
import com.caixy.shortlink.model.dto.group.GroupUpdateInfoRequest;
import com.caixy.shortlink.model.entity.Group;

import com.caixy.shortlink.model.entity.Link;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.group.GroupItemVO;
import com.caixy.shortlink.model.vo.group.GroupVO;

import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.GroupService;
import com.caixy.shortlink.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 分组信息服务实现
 *
 * @author: CAIXYPROMISE
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService, InitializingBean
{
    private final LinkMapper linkMapper;
    private final RedissonClient redissonClient;
    private RBloomFilter<String> gidBloomFilter;
    private final AuthManager authManager;

    /**
     * 校验数据
     *
     * @param group
     * @param add   对创建的数据进行校验
     */
    @Override
    public void validGroup(Group group, boolean add)
    {
        ThrowUtils.throwIf(group == null, ErrorCode.PARAMS_ERROR);

    }

    @Override
    public String addGroup(GroupAddRequest groupAddRequest, UserVO userVO)
    {
        checkGroupNameExist(groupAddRequest.getGroupName(), userVO.getNickName());
        // 生成组标识符
        String gid;
        do
        {
            gid = RandomUtil.randomString(10);
        } while (checkGidExist(gid));
        // 查找当前用户一共多少个分组
        // todo: 后续实现限制用户创建的分组数量。
        long count = baseMapper.countGroupsByNickname();
        Group group = Group.builder()
                           .name(groupAddRequest.getGroupName())
                           .gid(gid)
                           .description(groupAddRequest.getDescription())
                           .username(userVO.getNickName())
                           .sortOrder((int) count) // 设置排序, 从0开始计算
                           .build();
        boolean saved = this.save(group);
        if (!saved)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加分组失败");
        }
        return group.getGid();
    }


    @Override
    public Boolean updateGroupInfoByGid(UserVO loginUser, GroupUpdateInfoRequest groupUpdateInfoRequest)
    {
        // 检查是否为空
        LambdaQueryWrapper<Group> groupQueryWrapper = new LambdaQueryWrapper<>();
        groupQueryWrapper.eq(Group::getGid, groupUpdateInfoRequest.getGid())
                         .eq(Group::getUsername, loginUser.getNickName());
        Group group = baseMapper.selectOne(groupQueryWrapper);
        if (group == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分组不存在");
        }
        // 设置新的数据
        Optional.ofNullable(groupUpdateInfoRequest.getName()).ifPresent(group::setName);
        Optional.ofNullable(groupUpdateInfoRequest.getDescription()).ifPresent(group::setDescription);
        return this.updateById(group);
    }


    @Override
    public List<GroupItemVO> getMyGroupItems(String nickName)
    {
        if (StringUtils.isBlank(nickName))
        {
            return Collections.emptyList();
        }
        return baseMapper.findGroupsWithLinkCounts(nickName);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderByGid(String gid, int offset, UserVO loginUser)
    {
        Group groupToMove = findByGidAndCheckAccess(gid, loginUser);
        // 获取偏移量
        int oldIndex = groupToMove.getSortOrder();
        // 计算新的排序索引
        int newIndex = oldIndex + offset;
        if (offset > 0)
        {
            baseMapper.decreaseIndexInRange(oldIndex, newIndex);
        }
        else
        {
            baseMapper.increaseIndexInRange(newIndex, oldIndex);
        }
        groupToMove.setSortOrder(newIndex);
        return baseMapper.updateById(groupToMove) > 0;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteGroup(String gid, UserVO loginUser, String moveGroupId)
    {
        int updated = 0;
        Group groupToDelete = findByGidAndCheckAccess(gid, loginUser);
        // 检查分组下是否有链接
        LambdaQueryWrapper<Link> linkLambdaQueryWrapper = new LambdaQueryWrapper<>();
        linkLambdaQueryWrapper.eq(Link::getGid, groupToDelete.getGid())
                              .eq(Link::getIsDeleted, CommonConstant.NOT_DELETE_FLAG);
        List<Link> groupCount = linkMapper.selectList(linkLambdaQueryWrapper);
        // 如果原来的分组有链接，但是目标分组为空，则跑错
        if (!groupCount.isEmpty())
        {
            if (StringUtils.isBlank(moveGroupId))
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "原分组下存在链接，请选择要移动到的分组");
            }
            // 检查新的分组是否存在
            Group moveGroup = findByGidAndCheckAccess(moveGroupId, loginUser);
            // 将链接移动到新的分组
            int updateResult = linkMapper.updateGidByOldGid(groupToDelete.getGid(), moveGroup.getGid());
            if (updateResult == 0 || updateResult != groupCount.size())
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "移动链接失败");
            }
            updated = updateResult;
        }
        // 如果原来的分组没链接，则继续正常的逻辑往下走
        baseMapper.deleteById(groupToDelete.getId());
        baseMapper.updateIndexAfterDelete(groupToDelete.getSortOrder());
        return updated;
    }


    @Override
    public Page<GroupVO> getMyGroupList(UserVO userVO, GroupQueryRequest groupQueryRequest)
    {
        long current = groupQueryRequest.getCurrent();
        long size = groupQueryRequest.getPageSize();
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        log.info("查询我的分组列表，当前页码：{}，每页大小：{}, 用户信息: {}", current, size, userVO);
        // 设置查询条件
        queryWrapper.eq(Group::getUsername, userVO.getNickName());
        queryWrapper.orderByDesc(Group::getSortOrder);
        queryWrapper.orderByDesc(Group::getCreateTime);
        queryWrapper.eq(Group::getIsDeleted, CommonConstant.NOT_DELETE_FLAG);
        // 分页查询
        Page<Group> page = page(new Page<>(current, size), queryWrapper);
        return GroupConvertor.INSTANCE.copyVOPage(page);
    }


    @Override
    public QueryWrapper<Group> getQueryWrapper(GroupQueryRequest groupQueryRequest)
    {
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        if (groupQueryRequest == null)
        {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = groupQueryRequest.getId();
        Long notId = groupQueryRequest.getNotId();
        String title = groupQueryRequest.getTitle();
        String content = groupQueryRequest.getContent();
        String searchText = groupQueryRequest.getSearchText();
        String sortField = groupQueryRequest.getSortField();
        String sortOrder = groupQueryRequest.getSortOrder();
        List<String> tagList = groupQueryRequest.getTags();
        Long userId = groupQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText))
        {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList))
        {
            for (String tag : tagList)
            {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取分组信息封装
     *
     * @param gid
     * @param userVO
     * @return
     */
    @Override
    public GroupVO getGroupVO(String gid, UserVO userVO)
    {
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getGid, gid)
                    .eq(Group::getUsername, userVO.getNickName());
        Group group = this.baseMapper.selectOne(queryWrapper);
        if (group == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return GroupConvertor.INSTANCE.copyVO(group);
    }

    /**
     * 分页获取分组信息封装
     *
     * @param groupPage
     * @param request
     * @return
     */
    @Override
    public Page<GroupVO> getGroupVOPage(Page<Group> groupPage, HttpServletRequest request)
    {
        // todo: 补充分页获取分组信息封装逻辑
        return null;
    }

    /**
     * 检查 分组是否属于当前用户
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/13 21:12
     */
    @Override
    public void checkGroupBelongToUser(String gid)
    {
        UserVO loginUser = authManager.getLoginUser();
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getGid, gid)
                    .eq(Group::getUsername, loginUser.getNickName());
        List<Group> groupList = this.baseMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupList))
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户信息与分组标识不匹配");
        }
    }

    private boolean checkGidExist(String gid)
    {
        if (StringUtils.isNotBlank(gid) && gidBloomFilter.contains(gid))
        {
            return this.baseMapper.findGroupByGid(gid) != null;
        }
        return false;
    }

    /**
     * 检查团队名称是否存在
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/15 1:47
     */
    private void checkGroupNameExist(String groupName, String nickName)
    {
        if (StringUtils.isBlank(groupName))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分组名称不能为空");
        }
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getName, groupName);
        queryWrapper.eq(Group::getUsername, nickName);
        queryWrapper.eq(Group::getIsDeleted, CommonConstant.NOT_DELETE_FLAG);
        if (this.count(queryWrapper) > 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分组名称已存在");
        }
    }


    @Override
    public Group findByGidAndCheckAccess(String gid, UserVO loginUser)
    {
        Group byGidAndNickname = baseMapper.findGroupByGid(gid);
        if (byGidAndNickname == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分组不存在");
        }
        // 检查是否具有权限：创建者或系统管理员
        boolean isCreator = byGidAndNickname.getUsername().equals(loginUser.getNickName());
        boolean isAdmin = UserRoleEnum.ADMIN.equals(loginUser.getUserRole());
        if (!isCreator && !isAdmin)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "没有操作权限");
        }
        return byGidAndNickname;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.gidBloomFilter = redissonClient.getBloomFilter("gidFilter");
        if (gidBloomFilter != null && !gidBloomFilter.isExists())
        {
            this.gidBloomFilter.tryInit(100000L, 0.01);
        }
    }
}
