package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.group.GroupAddRequest;
import com.caixy.shortlink.model.dto.group.GroupQueryRequest;
import com.caixy.shortlink.model.dto.group.GroupUpdateInfoRequest;
import com.caixy.shortlink.model.entity.Group;
import com.caixy.shortlink.model.vo.group.GroupItemVO;
import com.caixy.shortlink.model.vo.group.GroupVO;

import com.caixy.shortlink.model.vo.user.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分组信息服务
 * @author: CAIXYPROMISE
*/
public interface GroupService extends IService<Group> {

    /**
     * 校验数据
     *
     * @param group
     * @param add   对创建的数据进行校验
     */
    void validGroup(Group group, boolean add);

    /**
     * 添加组
     *
     * @return 返回组ID
     * @author CAIXYPROMISE
     * @version 1.0
     */
    String addGroup(GroupAddRequest groupAddRequest, UserVO userVO);

    /**
     * 更新组名称
     *
     * @author CAIXYPROMISE
     * @version 1.0
     */
    Boolean updateGroupInfoByGid(UserVO loginUser, GroupUpdateInfoRequest groupUpdateInfoRequest);

    /**
     * 获取分组列表选项（侧边栏选项）
     *
     * @author CAIXYPROMISE
     * @version 1.0
     */
    List<GroupItemVO> getMyGroupItems(String nickName);

    /**
     * 更新分组
     *
     * @return
     * @author CAIXYPROMISE
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateOrderByGid(String gid, int offset, UserVO loginUser);

    /**
     * 删除分组
     *
     * @return
     * @author CAIXYPROMISE
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    int deleteGroup(String gid, UserVO loginUser, String moveGroup);

    /**
     * 分页获取我的分组列表
     *
     * @author CAIXYPROMISE
     * @version 1.0
     */
    Page<GroupVO> getMyGroupList(UserVO userVO, GroupQueryRequest groupQueryRequest);

    /**
     * 获取查询条件
     *
     * @param groupQueryRequest
     * @return
     */
    QueryWrapper<Group> getQueryWrapper(GroupQueryRequest groupQueryRequest);
    
    /**
     * 获取分组信息封装
     *
     * @param gid
     * @param userVO
     * @return
     */
    GroupVO getGroupVO(String gid, UserVO userVO);

    /**
     * 分页获取分组信息封装
     *
     *
     * @param groupPage
     * @param request
     */
    Page<GroupVO> getGroupVOPage(Page<Group> groupPage, HttpServletRequest request);

    void checkGroupBelongToUser(String gid);

    /**
     * 根据组id查找分组, 并且检查操作权限
     * todo: 实现分组管理权检查
     * @author CAIXYPROMISE
     */
    Group findByGidAndCheckAccess(String gid, UserVO loginUser);
}
