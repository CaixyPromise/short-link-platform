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
import com.caixy.shortlink.model.dto.linkDeviceStats.LinkDeviceStatsAddRequest;
import com.caixy.shortlink.model.dto.linkDeviceStats.LinkDeviceStatsEditRequest;
import com.caixy.shortlink.model.dto.linkDeviceStats.LinkDeviceStatsQueryRequest;
import com.caixy.shortlink.model.dto.linkDeviceStats.LinkDeviceStatsUpdateRequest;
import com.caixy.shortlink.model.entity.LinkDeviceStats;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkDeviceStats.LinkDeviceStatsVO;
import com.caixy.shortlink.service.LinkDeviceStatsService;
import com.caixy.shortlink.manager.authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接设备统计接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/linkDeviceStats")
public class LinkDeviceStatsController {

    private final LinkDeviceStatsService linkDeviceStatsService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接设备统计
     *
     * @param linkDeviceStatsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLinkDeviceStats(@RequestBody LinkDeviceStatsAddRequest linkDeviceStatsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkDeviceStatsAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        LinkDeviceStats linkDeviceStats = new LinkDeviceStats();
        BeanUtils.copyProperties(linkDeviceStatsAddRequest, linkDeviceStats);
        // 数据校验
        linkDeviceStatsService.validLinkDeviceStats(linkDeviceStats, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // linkDeviceStats.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkDeviceStatsService.save(linkDeviceStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkDeviceStatsId = linkDeviceStats.getId();
        return ResultUtils.success(newLinkDeviceStatsId);
    }

    /**
     * 删除短链接设备统计
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLinkDeviceStats(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        LinkDeviceStats oldLinkDeviceStats = linkDeviceStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkDeviceStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLinkDeviceStats);
        // 操作数据库
        boolean result = linkDeviceStatsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接设备统计（仅管理员可用）
     *
     * @param linkDeviceStatsUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLinkDeviceStats(@RequestBody LinkDeviceStatsUpdateRequest linkDeviceStatsUpdateRequest) {
        if (linkDeviceStatsUpdateRequest == null || linkDeviceStatsUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkDeviceStats linkDeviceStats = new LinkDeviceStats();
        BeanUtils.copyProperties(linkDeviceStatsUpdateRequest, linkDeviceStats);
        // 数据校验
        linkDeviceStatsService.validLinkDeviceStats(linkDeviceStats, false);
        // 判断是否存在
        long id = linkDeviceStatsUpdateRequest.getId();
        LinkDeviceStats oldLinkDeviceStats = linkDeviceStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkDeviceStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkDeviceStatsService.updateById(linkDeviceStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接设备统计（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkDeviceStatsVO> getLinkDeviceStatsVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        LinkDeviceStats linkDeviceStats = linkDeviceStatsService.getById(id);
        ThrowUtils.throwIf(linkDeviceStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkDeviceStatsService.getLinkDeviceStatsVO(linkDeviceStats, request));
    }

    /**
     * 分页获取短链接设备统计列表（仅管理员可用）
     *
     * @param linkDeviceStatsQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<LinkDeviceStats>> listLinkDeviceStatsByPage(@RequestBody LinkDeviceStatsQueryRequest linkDeviceStatsQueryRequest) {
        long current = linkDeviceStatsQueryRequest.getCurrent();
        long size = linkDeviceStatsQueryRequest.getPageSize();
        // 查询数据库
        Page<LinkDeviceStats> linkDeviceStatsPage = linkDeviceStatsService.page(new Page<>(current, size),
                linkDeviceStatsService.getQueryWrapper(linkDeviceStatsQueryRequest));
        return ResultUtils.success(linkDeviceStatsPage);
    }

    /**
     * 分页获取短链接设备统计列表（封装类）
     *
     * @param linkDeviceStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkDeviceStatsVO>> listLinkDeviceStatsVOByPage(@RequestBody LinkDeviceStatsQueryRequest linkDeviceStatsQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkDeviceStatsQueryRequest.getCurrent();
        long size = linkDeviceStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkDeviceStats> linkDeviceStatsPage = linkDeviceStatsService.page(new Page<>(current, size),
                linkDeviceStatsService.getQueryWrapper(linkDeviceStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkDeviceStatsService.getLinkDeviceStatsVOPage(linkDeviceStatsPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接设备统计列表
     *
     * @param linkDeviceStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkDeviceStatsVO>> listMyLinkDeviceStatsVOByPage(@RequestBody LinkDeviceStatsQueryRequest linkDeviceStatsQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkDeviceStatsQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkDeviceStatsQueryRequest.setUserId(loginUser.getId());
        long current = linkDeviceStatsQueryRequest.getCurrent();
        long size = linkDeviceStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkDeviceStats> linkDeviceStatsPage = linkDeviceStatsService.page(new Page<>(current, size),
                linkDeviceStatsService.getQueryWrapper(linkDeviceStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkDeviceStatsService.getLinkDeviceStatsVOPage(linkDeviceStatsPage, request));
    }

    /**
     * 编辑短链接设备统计（给用户使用）
     *
     * @param linkDeviceStatsEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLinkDeviceStats(@RequestBody LinkDeviceStatsEditRequest linkDeviceStatsEditRequest, HttpServletRequest request) {
        if (linkDeviceStatsEditRequest == null || linkDeviceStatsEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkDeviceStats linkDeviceStats = new LinkDeviceStats();
        BeanUtils.copyProperties(linkDeviceStatsEditRequest, linkDeviceStats);
        // 数据校验
        linkDeviceStatsService.validLinkDeviceStats(linkDeviceStats, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkDeviceStatsEditRequest.getId();
        LinkDeviceStats oldLinkDeviceStats = linkDeviceStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkDeviceStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLinkDeviceStats);
        // 操作数据库
        boolean result = linkDeviceStatsService.updateById(linkDeviceStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(LinkDeviceStats linkDeviceStats)
    // {
    //     if (!linkDeviceStats.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
