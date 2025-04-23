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
import com.caixy.shortlink.model.dto.linkAccessStats.LinkAccessStatsAddRequest;
import com.caixy.shortlink.model.dto.linkAccessStats.LinkAccessStatsEditRequest;
import com.caixy.shortlink.model.dto.linkAccessStats.LinkAccessStatsQueryRequest;
import com.caixy.shortlink.model.dto.linkAccessStats.LinkAccessStatsUpdateRequest;
import com.caixy.shortlink.model.entity.LinkAccessStats;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkAccessStats.LinkAccessStatsVO;
import com.caixy.shortlink.service.LinkAccessStatsService;
import com.caixy.shortlink.manager.authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接访问统计接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/linkAccessStats")
public class LinkAccessStatsController {

    private final LinkAccessStatsService linkAccessStatsService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接访问统计
     *
     * @param linkAccessStatsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLinkAccessStats(@RequestBody LinkAccessStatsAddRequest linkAccessStatsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkAccessStatsAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        LinkAccessStats linkAccessStats = new LinkAccessStats();
        BeanUtils.copyProperties(linkAccessStatsAddRequest, linkAccessStats);
        // 数据校验
        linkAccessStatsService.validLinkAccessStats(linkAccessStats, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // linkAccessStats.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkAccessStatsService.save(linkAccessStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkAccessStatsId = linkAccessStats.getId();
        return ResultUtils.success(newLinkAccessStatsId);
    }

    /**
     * 删除短链接访问统计
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLinkAccessStats(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        LinkAccessStats oldLinkAccessStats = linkAccessStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkAccessStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLinkAccessStats);
        // 操作数据库
        boolean result = linkAccessStatsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接访问统计（仅管理员可用）
     *
     * @param linkAccessStatsUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLinkAccessStats(@RequestBody LinkAccessStatsUpdateRequest linkAccessStatsUpdateRequest) {
        if (linkAccessStatsUpdateRequest == null || linkAccessStatsUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkAccessStats linkAccessStats = new LinkAccessStats();
        BeanUtils.copyProperties(linkAccessStatsUpdateRequest, linkAccessStats);
        // 数据校验
        linkAccessStatsService.validLinkAccessStats(linkAccessStats, false);
        // 判断是否存在
        long id = linkAccessStatsUpdateRequest.getId();
        LinkAccessStats oldLinkAccessStats = linkAccessStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkAccessStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkAccessStatsService.updateById(linkAccessStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接访问统计（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkAccessStatsVO> getLinkAccessStatsVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        LinkAccessStats linkAccessStats = linkAccessStatsService.getById(id);
        ThrowUtils.throwIf(linkAccessStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkAccessStatsService.getLinkAccessStatsVO(linkAccessStats, request));
    }

    /**
     * 分页获取短链接访问统计列表（仅管理员可用）
     *
     * @param linkAccessStatsQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<LinkAccessStats>> listLinkAccessStatsByPage(@RequestBody LinkAccessStatsQueryRequest linkAccessStatsQueryRequest) {
        long current = linkAccessStatsQueryRequest.getCurrent();
        long size = linkAccessStatsQueryRequest.getPageSize();
        // 查询数据库
        Page<LinkAccessStats> linkAccessStatsPage = linkAccessStatsService.page(new Page<>(current, size),
                linkAccessStatsService.getQueryWrapper(linkAccessStatsQueryRequest));
        return ResultUtils.success(linkAccessStatsPage);
    }

    /**
     * 分页获取短链接访问统计列表（封装类）
     *
     * @param linkAccessStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkAccessStatsVO>> listLinkAccessStatsVOByPage(@RequestBody LinkAccessStatsQueryRequest linkAccessStatsQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkAccessStatsQueryRequest.getCurrent();
        long size = linkAccessStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkAccessStats> linkAccessStatsPage = linkAccessStatsService.page(new Page<>(current, size),
                linkAccessStatsService.getQueryWrapper(linkAccessStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkAccessStatsService.getLinkAccessStatsVOPage(linkAccessStatsPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接访问统计列表
     *
     * @param linkAccessStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkAccessStatsVO>> listMyLinkAccessStatsVOByPage(@RequestBody LinkAccessStatsQueryRequest linkAccessStatsQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkAccessStatsQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkAccessStatsQueryRequest.setUserId(loginUser.getId());
        long current = linkAccessStatsQueryRequest.getCurrent();
        long size = linkAccessStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkAccessStats> linkAccessStatsPage = linkAccessStatsService.page(new Page<>(current, size),
                linkAccessStatsService.getQueryWrapper(linkAccessStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkAccessStatsService.getLinkAccessStatsVOPage(linkAccessStatsPage, request));
    }

    /**
     * 编辑短链接访问统计（给用户使用）
     *
     * @param linkAccessStatsEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLinkAccessStats(@RequestBody LinkAccessStatsEditRequest linkAccessStatsEditRequest, HttpServletRequest request) {
        if (linkAccessStatsEditRequest == null || linkAccessStatsEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkAccessStats linkAccessStats = new LinkAccessStats();
        BeanUtils.copyProperties(linkAccessStatsEditRequest, linkAccessStats);
        // 数据校验
        linkAccessStatsService.validLinkAccessStats(linkAccessStats, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkAccessStatsEditRequest.getId();
        LinkAccessStats oldLinkAccessStats = linkAccessStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkAccessStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLinkAccessStats);
        // 操作数据库
        boolean result = linkAccessStatsService.updateById(linkAccessStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(LinkAccessStats linkAccessStats)
    // {
    //     if (!linkAccessStats.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
