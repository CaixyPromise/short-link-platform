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
import com.caixy.shortlink.mapper.GroupMapper;
import com.caixy.shortlink.model.convertor.group.GroupConvertor;
import com.caixy.shortlink.model.dto.group.GroupAddRequest;
import com.caixy.shortlink.model.dto.group.GroupQueryRequest;
import com.caixy.shortlink.model.entity.Group;

import com.caixy.shortlink.model.vo.group.GroupItemVO;
import com.caixy.shortlink.model.vo.group.GroupVO;

import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.GroupService;
import com.caixy.shortlink.service.UserService;
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

import java.util.Collections;
import java.util.List;

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
    private final UserService userService;
    private final RedissonClient redissonClient;
    private RBloomFilter<String> gidBloomFilter;

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


    /**
     * 添加组
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/15 1:50
     */
    @Override
    public boolean addGroup(GroupAddRequest groupAddRequest, UserVO userVO)
    {
        checkGroupNameExist(groupAddRequest.getGroupName(), userVO.getNickName());
        // 生成组标识符
        String gid;
        do
        {
            gid = RandomUtil.randomString(10);
        } while (checkGidExist(gid));
        Group group = Group.builder()
                           .name(groupAddRequest.getGroupName())
                           .gid(gid)
                           .description(groupAddRequest.getDescription())
                           .username(userVO.getNickName())
                           .sortOrder(groupAddRequest.getSortOrder())
                           .build();
        return this.save(group);
    }

    /**
     * 获取分组列表选项
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/21 0:20
     */
    @Override
    public List<GroupItemVO> getMyGroupItems(String nickName)
    {
        if (StringUtils.isBlank(nickName)) {
            return Collections.emptyList();
        }
        return baseMapper.findGroupsWithLinkCounts(nickName);
    }

    /**
     * 获取我的分组列表
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/15 16:45
     */
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


    /**
     * 获取查询条件
     *
     * @param groupQueryRequest
     * @return
     */
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
     * @param group
     * @param request
     * @return
     */
    @Override
    public GroupVO getGroupVO(Group group, HttpServletRequest request)
    {
        // todo: 补充获取分组信息封装逻辑
        return null;
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

    public boolean checkGidExist(String gid)
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
    public void checkGroupNameExist(String groupName, String nickName)
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
    public void afterPropertiesSet() throws Exception
    {
        this.gidBloomFilter = redissonClient.getBloomFilter("gidFilter");
        if (gidBloomFilter != null && !gidBloomFilter.isExists())
        {
            this.gidBloomFilter.tryInit(100000L, 0.01);
        }
    }
}
