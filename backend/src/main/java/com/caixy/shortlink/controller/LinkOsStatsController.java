package com.caixy.shortlink.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.shortlink.annotation.AuthCheck;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.DeleteRequest;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.model.dto.linkOsStats.LinkOsStatsAddRequest;
import com.caixy.shortlink.model.dto.linkOsStats.LinkOsStatsEditRequest;
import com.caixy.shortlink.model.dto.linkOsStats.LinkOsStatsQueryRequest;
import com.caixy.shortlink.model.dto.linkOsStats.LinkOsStatsUpdateRequest;
import com.caixy.shortlink.model.entity.LinkOsStats;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkOsStats.LinkOsStatsVO;
import com.caixy.shortlink.service.LinkOsStatsService;
import com.caixy.shortlink.manager.authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接操作系统统计接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/linkOsStats")
public class LinkOsStatsController {

    private final LinkOsStatsService linkOsStatsService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接操作系统统计
     *
     * @param linkOsStatsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLinkOsStats(@RequestBody LinkOsStatsAddRequest linkOsStatsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkOsStatsAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        LinkOsStats linkOsStats = new LinkOsStats();
        BeanUtils.copyProperties(linkOsStatsAddRequest, linkOsStats);
        // 数据校验
        linkOsStatsService.validLinkOsStats(linkOsStats, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // linkOsStats.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkOsStatsService.save(linkOsStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkOsStatsId = linkOsStats.getId();
        return ResultUtils.success(newLinkOsStatsId);
    }

    /**
     * 删除短链接操作系统统计
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLinkOsStats(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        LinkOsStats oldLinkOsStats = linkOsStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkOsStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLinkOsStats);
        // 操作数据库
        boolean result = linkOsStatsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接操作系统统计（仅管理员可用）
     *
     * @param linkOsStatsUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLinkOsStats(@RequestBody LinkOsStatsUpdateRequest linkOsStatsUpdateRequest) {
        if (linkOsStatsUpdateRequest == null || linkOsStatsUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkOsStats linkOsStats = new LinkOsStats();
        BeanUtils.copyProperties(linkOsStatsUpdateRequest, linkOsStats);
        // 数据校验
        linkOsStatsService.validLinkOsStats(linkOsStats, false);
        // 判断是否存在
        long id = linkOsStatsUpdateRequest.getId();
        LinkOsStats oldLinkOsStats = linkOsStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkOsStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkOsStatsService.updateById(linkOsStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接操作系统统计（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkOsStatsVO> getLinkOsStatsVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        LinkOsStats linkOsStats = linkOsStatsService.getById(id);
        ThrowUtils.throwIf(linkOsStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkOsStatsService.getLinkOsStatsVO(linkOsStats, request));
    }

    /**
     * 分页获取短链接操作系统统计列表（仅管理员可用）
     *
     * @param linkOsStatsQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<LinkOsStats>> listLinkOsStatsByPage(@RequestBody LinkOsStatsQueryRequest linkOsStatsQueryRequest) {
        long current = linkOsStatsQueryRequest.getCurrent();
        long size = linkOsStatsQueryRequest.getPageSize();
        // 查询数据库
        Page<LinkOsStats> linkOsStatsPage = linkOsStatsService.page(new Page<>(current, size),
                linkOsStatsService.getQueryWrapper(linkOsStatsQueryRequest));
        return ResultUtils.success(linkOsStatsPage);
    }

    /**
     * 分页获取短链接操作系统统计列表（封装类）
     *
     * @param linkOsStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkOsStatsVO>> listLinkOsStatsVOByPage(@RequestBody LinkOsStatsQueryRequest linkOsStatsQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkOsStatsQueryRequest.getCurrent();
        long size = linkOsStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkOsStats> linkOsStatsPage = linkOsStatsService.page(new Page<>(current, size),
                linkOsStatsService.getQueryWrapper(linkOsStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkOsStatsService.getLinkOsStatsVOPage(linkOsStatsPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接操作系统统计列表
     *
     * @param linkOsStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkOsStatsVO>> listMyLinkOsStatsVOByPage(@RequestBody LinkOsStatsQueryRequest linkOsStatsQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkOsStatsQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkOsStatsQueryRequest.setUserId(loginUser.getId());
        long current = linkOsStatsQueryRequest.getCurrent();
        long size = linkOsStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkOsStats> linkOsStatsPage = linkOsStatsService.page(new Page<>(current, size),
                linkOsStatsService.getQueryWrapper(linkOsStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkOsStatsService.getLinkOsStatsVOPage(linkOsStatsPage, request));
    }

    /**
     * 编辑短链接操作系统统计（给用户使用）
     *
     * @param linkOsStatsEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLinkOsStats(@RequestBody LinkOsStatsEditRequest linkOsStatsEditRequest, HttpServletRequest request) {
        if (linkOsStatsEditRequest == null || linkOsStatsEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkOsStats linkOsStats = new LinkOsStats();
        BeanUtils.copyProperties(linkOsStatsEditRequest, linkOsStats);
        // 数据校验
        linkOsStatsService.validLinkOsStats(linkOsStats, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkOsStatsEditRequest.getId();
        LinkOsStats oldLinkOsStats = linkOsStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkOsStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLinkOsStats);
        // 操作数据库
        boolean result = linkOsStatsService.updateById(linkOsStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(LinkOsStats linkOsStats)
    // {
    //     if (!linkOsStats.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
