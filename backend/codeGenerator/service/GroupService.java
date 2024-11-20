package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.group.GroupQueryRequest;
import com.caixy.shortlink.model.entity.Group;
import com.caixy.shortlink.model.vo.group.GroupVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 分组信息服务
 * @author: CAIXYPROMISE
*/
public interface GroupService extends IService<Group> {

    /**
     * 校验数据
     *
     * @param group
     * @param add 对创建的数据进行校验
     */
    void validGroup(Group group, boolean add);

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
     * @param group
     * @param request
     * @return
     */
    GroupVO getGroupVO(Group group, HttpServletRequest request);

    /**
     * 分页获取分组信息封装
     *
     * @param groupPage
     * @param request
     * @return
     */
    Page<GroupVO> getGroupVOPage(Page<Group> groupPage, HttpServletRequest request);
}
