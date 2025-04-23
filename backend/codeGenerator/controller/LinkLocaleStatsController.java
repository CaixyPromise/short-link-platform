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
import com.caixy.shortlink.model.dto.linkLocaleStats.LinkLocaleStatsAddRequest;
import com.caixy.shortlink.model.dto.linkLocaleStats.LinkLocaleStatsEditRequest;
import com.caixy.shortlink.model.dto.linkLocaleStats.LinkLocaleStatsQueryRequest;
import com.caixy.shortlink.model.dto.linkLocaleStats.LinkLocaleStatsUpdateRequest;
import com.caixy.shortlink.model.entity.LinkLocaleStats;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkLocaleStats.LinkLocaleStatsVO;
import com.caixy.shortlink.service.LinkLocaleStatsService;
import com.caixy.shortlink.manager.authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接地域统计接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/linkLocaleStats")
public class LinkLocaleStatsController {

    private final LinkLocaleStatsService linkLocaleStatsService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接地域统计
     *
     * @param linkLocaleStatsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLinkLocaleStats(@RequestBody LinkLocaleStatsAddRequest linkLocaleStatsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkLocaleStatsAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        LinkLocaleStats linkLocaleStats = new LinkLocaleStats();
        BeanUtils.copyProperties(linkLocaleStatsAddRequest, linkLocaleStats);
        // 数据校验
        linkLocaleStatsService.validLinkLocaleStats(linkLocaleStats, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // linkLocaleStats.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkLocaleStatsService.save(linkLocaleStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkLocaleStatsId = linkLocaleStats.getId();
        return ResultUtils.success(newLinkLocaleStatsId);
    }

    /**
     * 删除短链接地域统计
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLinkLocaleStats(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        LinkLocaleStats oldLinkLocaleStats = linkLocaleStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkLocaleStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLinkLocaleStats);
        // 操作数据库
        boolean result = linkLocaleStatsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接地域统计（仅管理员可用）
     *
     * @param linkLocaleStatsUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLinkLocaleStats(@RequestBody LinkLocaleStatsUpdateRequest linkLocaleStatsUpdateRequest) {
        if (linkLocaleStatsUpdateRequest == null || linkLocaleStatsUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkLocaleStats linkLocaleStats = new LinkLocaleStats();
        BeanUtils.copyProperties(linkLocaleStatsUpdateRequest, linkLocaleStats);
        // 数据校验
        linkLocaleStatsService.validLinkLocaleStats(linkLocaleStats, false);
        // 判断是否存在
        long id = linkLocaleStatsUpdateRequest.getId();
        LinkLocaleStats oldLinkLocaleStats = linkLocaleStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkLocaleStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkLocaleStatsService.updateById(linkLocaleStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接地域统计（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkLocaleStatsVO> getLinkLocaleStatsVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        LinkLocaleStats linkLocaleStats = linkLocaleStatsService.getById(id);
        ThrowUtils.throwIf(linkLocaleStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkLocaleStatsService.getLinkLocaleStatsVO(linkLocaleStats, request));
    }

    /**
     * 分页获取短链接地域统计列表（仅管理员可用）
     *
     * @param linkLocaleStatsQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<LinkLocaleStats>> listLinkLocaleStatsByPage(@RequestBody LinkLocaleStatsQueryRequest linkLocaleStatsQueryRequest) {
        long current = linkLocaleStatsQueryRequest.getCurrent();
        long size = linkLocaleStatsQueryRequest.getPageSize();
        // 查询数据库
        Page<LinkLocaleStats> linkLocaleStatsPage = linkLocaleStatsService.page(new Page<>(current, size),
                linkLocaleStatsService.getQueryWrapper(linkLocaleStatsQueryRequest));
        return ResultUtils.success(linkLocaleStatsPage);
    }

    /**
     * 分页获取短链接地域统计列表（封装类）
     *
     * @param linkLocaleStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkLocaleStatsVO>> listLinkLocaleStatsVOByPage(@RequestBody LinkLocaleStatsQueryRequest linkLocaleStatsQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkLocaleStatsQueryRequest.getCurrent();
        long size = linkLocaleStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkLocaleStats> linkLocaleStatsPage = linkLocaleStatsService.page(new Page<>(current, size),
                linkLocaleStatsService.getQueryWrapper(linkLocaleStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkLocaleStatsService.getLinkLocaleStatsVOPage(linkLocaleStatsPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接地域统计列表
     *
     * @param linkLocaleStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkLocaleStatsVO>> listMyLinkLocaleStatsVOByPage(@RequestBody LinkLocaleStatsQueryRequest linkLocaleStatsQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkLocaleStatsQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkLocaleStatsQueryRequest.setUserId(loginUser.getId());
        long current = linkLocaleStatsQueryRequest.getCurrent();
        long size = linkLocaleStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkLocaleStats> linkLocaleStatsPage = linkLocaleStatsService.page(new Page<>(current, size),
                linkLocaleStatsService.getQueryWrapper(linkLocaleStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkLocaleStatsService.getLinkLocaleStatsVOPage(linkLocaleStatsPage, request));
    }

    /**
     * 编辑短链接地域统计（给用户使用）
     *
     * @param linkLocaleStatsEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLinkLocaleStats(@RequestBody LinkLocaleStatsEditRequest linkLocaleStatsEditRequest, HttpServletRequest request) {
        if (linkLocaleStatsEditRequest == null || linkLocaleStatsEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkLocaleStats linkLocaleStats = new LinkLocaleStats();
        BeanUtils.copyProperties(linkLocaleStatsEditRequest, linkLocaleStats);
        // 数据校验
        linkLocaleStatsService.validLinkLocaleStats(linkLocaleStats, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkLocaleStatsEditRequest.getId();
        LinkLocaleStats oldLinkLocaleStats = linkLocaleStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkLocaleStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLinkLocaleStats);
        // 操作数据库
        boolean result = linkLocaleStatsService.updateById(linkLocaleStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(LinkLocaleStats linkLocaleStats)
    // {
    //     if (!linkLocaleStats.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
