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
import com.caixy.shortlink.model.dto.linkNetworkStats.LinkNetworkStatsAddRequest;
import com.caixy.shortlink.model.dto.linkNetworkStats.LinkNetworkStatsEditRequest;
import com.caixy.shortlink.model.dto.linkNetworkStats.LinkNetworkStatsQueryRequest;
import com.caixy.shortlink.model.dto.linkNetworkStats.LinkNetworkStatsUpdateRequest;
import com.caixy.shortlink.model.entity.LinkNetworkStats;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkNetworkStats.LinkNetworkStatsVO;
import com.caixy.shortlink.service.LinkNetworkStatsService;
import com.caixy.shortlink.manager.authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接网络统计接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/linkNetworkStats")
public class LinkNetworkStatsController {

    private final LinkNetworkStatsService linkNetworkStatsService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接网络统计
     *
     * @param linkNetworkStatsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLinkNetworkStats(@RequestBody LinkNetworkStatsAddRequest linkNetworkStatsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkNetworkStatsAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        LinkNetworkStats linkNetworkStats = new LinkNetworkStats();
        BeanUtils.copyProperties(linkNetworkStatsAddRequest, linkNetworkStats);
        // 数据校验
        linkNetworkStatsService.validLinkNetworkStats(linkNetworkStats, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // linkNetworkStats.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkNetworkStatsService.save(linkNetworkStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkNetworkStatsId = linkNetworkStats.getId();
        return ResultUtils.success(newLinkNetworkStatsId);
    }

    /**
     * 删除短链接网络统计
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLinkNetworkStats(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        LinkNetworkStats oldLinkNetworkStats = linkNetworkStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkNetworkStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLinkNetworkStats);
        // 操作数据库
        boolean result = linkNetworkStatsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接网络统计（仅管理员可用）
     *
     * @param linkNetworkStatsUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLinkNetworkStats(@RequestBody LinkNetworkStatsUpdateRequest linkNetworkStatsUpdateRequest) {
        if (linkNetworkStatsUpdateRequest == null || linkNetworkStatsUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkNetworkStats linkNetworkStats = new LinkNetworkStats();
        BeanUtils.copyProperties(linkNetworkStatsUpdateRequest, linkNetworkStats);
        // 数据校验
        linkNetworkStatsService.validLinkNetworkStats(linkNetworkStats, false);
        // 判断是否存在
        long id = linkNetworkStatsUpdateRequest.getId();
        LinkNetworkStats oldLinkNetworkStats = linkNetworkStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkNetworkStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkNetworkStatsService.updateById(linkNetworkStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接网络统计（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkNetworkStatsVO> getLinkNetworkStatsVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        LinkNetworkStats linkNetworkStats = linkNetworkStatsService.getById(id);
        ThrowUtils.throwIf(linkNetworkStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkNetworkStatsService.getLinkNetworkStatsVO(linkNetworkStats, request));
    }

    /**
     * 分页获取短链接网络统计列表（仅管理员可用）
     *
     * @param linkNetworkStatsQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<LinkNetworkStats>> listLinkNetworkStatsByPage(@RequestBody LinkNetworkStatsQueryRequest linkNetworkStatsQueryRequest) {
        long current = linkNetworkStatsQueryRequest.getCurrent();
        long size = linkNetworkStatsQueryRequest.getPageSize();
        // 查询数据库
        Page<LinkNetworkStats> linkNetworkStatsPage = linkNetworkStatsService.page(new Page<>(current, size),
                linkNetworkStatsService.getQueryWrapper(linkNetworkStatsQueryRequest));
        return ResultUtils.success(linkNetworkStatsPage);
    }

    /**
     * 分页获取短链接网络统计列表（封装类）
     *
     * @param linkNetworkStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkNetworkStatsVO>> listLinkNetworkStatsVOByPage(@RequestBody LinkNetworkStatsQueryRequest linkNetworkStatsQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkNetworkStatsQueryRequest.getCurrent();
        long size = linkNetworkStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkNetworkStats> linkNetworkStatsPage = linkNetworkStatsService.page(new Page<>(current, size),
                linkNetworkStatsService.getQueryWrapper(linkNetworkStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkNetworkStatsService.getLinkNetworkStatsVOPage(linkNetworkStatsPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接网络统计列表
     *
     * @param linkNetworkStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkNetworkStatsVO>> listMyLinkNetworkStatsVOByPage(@RequestBody LinkNetworkStatsQueryRequest linkNetworkStatsQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkNetworkStatsQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkNetworkStatsQueryRequest.setUserId(loginUser.getId());
        long current = linkNetworkStatsQueryRequest.getCurrent();
        long size = linkNetworkStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkNetworkStats> linkNetworkStatsPage = linkNetworkStatsService.page(new Page<>(current, size),
                linkNetworkStatsService.getQueryWrapper(linkNetworkStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkNetworkStatsService.getLinkNetworkStatsVOPage(linkNetworkStatsPage, request));
    }

    /**
     * 编辑短链接网络统计（给用户使用）
     *
     * @param linkNetworkStatsEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLinkNetworkStats(@RequestBody LinkNetworkStatsEditRequest linkNetworkStatsEditRequest, HttpServletRequest request) {
        if (linkNetworkStatsEditRequest == null || linkNetworkStatsEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkNetworkStats linkNetworkStats = new LinkNetworkStats();
        BeanUtils.copyProperties(linkNetworkStatsEditRequest, linkNetworkStats);
        // 数据校验
        linkNetworkStatsService.validLinkNetworkStats(linkNetworkStats, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkNetworkStatsEditRequest.getId();
        LinkNetworkStats oldLinkNetworkStats = linkNetworkStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkNetworkStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLinkNetworkStats);
        // 操作数据库
        boolean result = linkNetworkStatsService.updateById(linkNetworkStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(LinkNetworkStats linkNetworkStats)
    // {
    //     if (!linkNetworkStats.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
