package com.caixy.shortlink.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.shortlink.annotation.AuthCheck;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.DeleteRequest;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.constant.UserConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.model.dto.group.GroupAddRequest;
import com.caixy.shortlink.model.dto.group.GroupEditRequest;
import com.caixy.shortlink.model.dto.group.GroupQueryRequest;
import com.caixy.shortlink.model.dto.group.GroupUpdateRequest;
import com.caixy.shortlink.model.entity.Group;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.group.GroupVO;
import com.caixy.shortlink.service.GroupService;
import com.caixy.shortlink.manager.authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 分组信息接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建分组信息
     *
     * @param groupAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addGroup(@RequestBody GroupAddRequest groupAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(groupAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Group group = new Group();
        BeanUtils.copyProperties(groupAddRequest, group);
        // 数据校验
        groupService.validGroup(group, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // group.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = groupService.save(group);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newGroupId = group.getId();
        return ResultUtils.success(newGroupId);
    }

    /**
     * 删除分组信息
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteGroup(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        Group oldGroup = groupService.getById(id);
        ThrowUtils.throwIf(oldGroup == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldGroup);
        // 操作数据库
        boolean result = groupService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新分组信息（仅管理员可用）
     *
     * @param groupUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateGroup(@RequestBody GroupUpdateRequest groupUpdateRequest) {
        if (groupUpdateRequest == null || groupUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Group group = new Group();
        BeanUtils.copyProperties(groupUpdateRequest, group);
        // 数据校验
        groupService.validGroup(group, false);
        // 判断是否存在
        long id = groupUpdateRequest.getId();
        Group oldGroup = groupService.getById(id);
        ThrowUtils.throwIf(oldGroup == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = groupService.updateById(group);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取分组信息（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<GroupVO> getGroupVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Group group = groupService.getById(id);
        ThrowUtils.throwIf(group == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(groupService.getGroupVO(group, request));
    }

    /**
     * 分页获取分组信息列表（仅管理员可用）
     *
     * @param groupQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<Group>> listGroupByPage(@RequestBody GroupQueryRequest groupQueryRequest) {
        long current = groupQueryRequest.getCurrent();
        long size = groupQueryRequest.getPageSize();
        // 查询数据库
        Page<Group> groupPage = groupService.page(new Page<>(current, size),
                groupService.getQueryWrapper(groupQueryRequest));
        return ResultUtils.success(groupPage);
    }

    /**
     * 分页获取分组信息列表（封装类）
     *
     * @param groupQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<GroupVO>> listGroupVOByPage(@RequestBody GroupQueryRequest groupQueryRequest,
                                                               HttpServletRequest request) {
        long current = groupQueryRequest.getCurrent();
        long size = groupQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Group> groupPage = groupService.page(new Page<>(current, size),
                groupService.getQueryWrapper(groupQueryRequest));
        // 获取封装类
        return ResultUtils.success(groupService.getGroupVOPage(groupPage, request));
    }

    /**
     * 分页获取当前登录用户创建的分组信息列表
     *
     * @param groupQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<GroupVO>> listMyGroupVOByPage(@RequestBody GroupQueryRequest groupQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(groupQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        groupQueryRequest.setUserId(loginUser.getId());
        long current = groupQueryRequest.getCurrent();
        long size = groupQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Group> groupPage = groupService.page(new Page<>(current, size),
                groupService.getQueryWrapper(groupQueryRequest));
        // 获取封装类
        return ResultUtils.success(groupService.getGroupVOPage(groupPage, request));
    }

    /**
     * 编辑分组信息（给用户使用）
     *
     * @param groupEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editGroup(@RequestBody GroupEditRequest groupEditRequest, HttpServletRequest request) {
        if (groupEditRequest == null || groupEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Group group = new Group();
        BeanUtils.copyProperties(groupEditRequest, group);
        // 数据校验
        groupService.validGroup(group, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = groupEditRequest.getId();
        Group oldGroup = groupService.getById(id);
        ThrowUtils.throwIf(oldGroup == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldGroup);
        // 操作数据库
        boolean result = groupService.updateById(group);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(Group group)
    // {
    //     if (!group.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
