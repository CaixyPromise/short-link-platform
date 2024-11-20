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
import com.caixy.shortlink.model.dto.linkAccessLogs.LinkAccessLogsAddRequest;
import com.caixy.shortlink.model.dto.linkAccessLogs.LinkAccessLogsEditRequest;
import com.caixy.shortlink.model.dto.linkAccessLogs.LinkAccessLogsQueryRequest;
import com.caixy.shortlink.model.dto.linkAccessLogs.LinkAccessLogsUpdateRequest;
import com.caixy.shortlink.model.entity.LinkAccessLogs;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkAccessLogs.LinkAccessLogsVO;
import com.caixy.shortlink.service.LinkAccessLogsService;
import com.caixy.shortlink.manager.Authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接访问日志接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/linkAccessLogs")
public class LinkAccessLogsController {

    private final LinkAccessLogsService linkAccessLogsService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接访问日志
     *
     * @param linkAccessLogsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLinkAccessLogs(@RequestBody LinkAccessLogsAddRequest linkAccessLogsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkAccessLogsAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        LinkAccessLogs linkAccessLogs = new LinkAccessLogs();
        BeanUtils.copyProperties(linkAccessLogsAddRequest, linkAccessLogs);
        // 数据校验
        linkAccessLogsService.validLinkAccessLogs(linkAccessLogs, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // linkAccessLogs.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkAccessLogsService.save(linkAccessLogs);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkAccessLogsId = linkAccessLogs.getId();
        return ResultUtils.success(newLinkAccessLogsId);
    }

    /**
     * 删除短链接访问日志
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLinkAccessLogs(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        LinkAccessLogs oldLinkAccessLogs = linkAccessLogsService.getById(id);
        ThrowUtils.throwIf(oldLinkAccessLogs == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLinkAccessLogs);
        // 操作数据库
        boolean result = linkAccessLogsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接访问日志（仅管理员可用）
     *
     * @param linkAccessLogsUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLinkAccessLogs(@RequestBody LinkAccessLogsUpdateRequest linkAccessLogsUpdateRequest) {
        if (linkAccessLogsUpdateRequest == null || linkAccessLogsUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkAccessLogs linkAccessLogs = new LinkAccessLogs();
        BeanUtils.copyProperties(linkAccessLogsUpdateRequest, linkAccessLogs);
        // 数据校验
        linkAccessLogsService.validLinkAccessLogs(linkAccessLogs, false);
        // 判断是否存在
        long id = linkAccessLogsUpdateRequest.getId();
        LinkAccessLogs oldLinkAccessLogs = linkAccessLogsService.getById(id);
        ThrowUtils.throwIf(oldLinkAccessLogs == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkAccessLogsService.updateById(linkAccessLogs);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接访问日志（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkAccessLogsVO> getLinkAccessLogsVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        LinkAccessLogs linkAccessLogs = linkAccessLogsService.getById(id);
        ThrowUtils.throwIf(linkAccessLogs == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkAccessLogsService.getLinkAccessLogsVO(linkAccessLogs, request));
    }

    /**
     * 分页获取短链接访问日志列表（仅管理员可用）
     *
     * @param linkAccessLogsQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<LinkAccessLogs>> listLinkAccessLogsByPage(@RequestBody LinkAccessLogsQueryRequest linkAccessLogsQueryRequest) {
        long current = linkAccessLogsQueryRequest.getCurrent();
        long size = linkAccessLogsQueryRequest.getPageSize();
        // 查询数据库
        Page<LinkAccessLogs> linkAccessLogsPage = linkAccessLogsService.page(new Page<>(current, size),
                linkAccessLogsService.getQueryWrapper(linkAccessLogsQueryRequest));
        return ResultUtils.success(linkAccessLogsPage);
    }

    /**
     * 分页获取短链接访问日志列表（封装类）
     *
     * @param linkAccessLogsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkAccessLogsVO>> listLinkAccessLogsVOByPage(@RequestBody LinkAccessLogsQueryRequest linkAccessLogsQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkAccessLogsQueryRequest.getCurrent();
        long size = linkAccessLogsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkAccessLogs> linkAccessLogsPage = linkAccessLogsService.page(new Page<>(current, size),
                linkAccessLogsService.getQueryWrapper(linkAccessLogsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkAccessLogsService.getLinkAccessLogsVOPage(linkAccessLogsPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接访问日志列表
     *
     * @param linkAccessLogsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkAccessLogsVO>> listMyLinkAccessLogsVOByPage(@RequestBody LinkAccessLogsQueryRequest linkAccessLogsQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkAccessLogsQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkAccessLogsQueryRequest.setUserId(loginUser.getId());
        long current = linkAccessLogsQueryRequest.getCurrent();
        long size = linkAccessLogsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkAccessLogs> linkAccessLogsPage = linkAccessLogsService.page(new Page<>(current, size),
                linkAccessLogsService.getQueryWrapper(linkAccessLogsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkAccessLogsService.getLinkAccessLogsVOPage(linkAccessLogsPage, request));
    }

    /**
     * 编辑短链接访问日志（给用户使用）
     *
     * @param linkAccessLogsEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLinkAccessLogs(@RequestBody LinkAccessLogsEditRequest linkAccessLogsEditRequest, HttpServletRequest request) {
        if (linkAccessLogsEditRequest == null || linkAccessLogsEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkAccessLogs linkAccessLogs = new LinkAccessLogs();
        BeanUtils.copyProperties(linkAccessLogsEditRequest, linkAccessLogs);
        // 数据校验
        linkAccessLogsService.validLinkAccessLogs(linkAccessLogs, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkAccessLogsEditRequest.getId();
        LinkAccessLogs oldLinkAccessLogs = linkAccessLogsService.getById(id);
        ThrowUtils.throwIf(oldLinkAccessLogs == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLinkAccessLogs);
        // 操作数据库
        boolean result = linkAccessLogsService.updateById(linkAccessLogs);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(LinkAccessLogs linkAccessLogs)
    // {
    //     if (!linkAccessLogs.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
