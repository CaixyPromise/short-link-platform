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
import com.caixy.shortlink.model.dto.linkBrowserStats.LinkBrowserStatsAddRequest;
import com.caixy.shortlink.model.dto.linkBrowserStats.LinkBrowserStatsEditRequest;
import com.caixy.shortlink.model.dto.linkBrowserStats.LinkBrowserStatsQueryRequest;
import com.caixy.shortlink.model.dto.linkBrowserStats.LinkBrowserStatsUpdateRequest;
import com.caixy.shortlink.model.entity.LinkBrowserStats;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkBrowserStats.LinkBrowserStatsVO;
import com.caixy.shortlink.service.LinkBrowserStatsService;
import com.caixy.shortlink.manager.Authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接浏览器统计接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/linkBrowserStats")
public class LinkBrowserStatsController {

    private final LinkBrowserStatsService linkBrowserStatsService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接浏览器统计
     *
     * @param linkBrowserStatsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLinkBrowserStats(@RequestBody LinkBrowserStatsAddRequest linkBrowserStatsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkBrowserStatsAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        LinkBrowserStats linkBrowserStats = new LinkBrowserStats();
        BeanUtils.copyProperties(linkBrowserStatsAddRequest, linkBrowserStats);
        // 数据校验
        linkBrowserStatsService.validLinkBrowserStats(linkBrowserStats, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // linkBrowserStats.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkBrowserStatsService.save(linkBrowserStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkBrowserStatsId = linkBrowserStats.getId();
        return ResultUtils.success(newLinkBrowserStatsId);
    }

    /**
     * 删除短链接浏览器统计
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLinkBrowserStats(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        LinkBrowserStats oldLinkBrowserStats = linkBrowserStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkBrowserStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLinkBrowserStats);
        // 操作数据库
        boolean result = linkBrowserStatsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接浏览器统计（仅管理员可用）
     *
     * @param linkBrowserStatsUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLinkBrowserStats(@RequestBody LinkBrowserStatsUpdateRequest linkBrowserStatsUpdateRequest) {
        if (linkBrowserStatsUpdateRequest == null || linkBrowserStatsUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkBrowserStats linkBrowserStats = new LinkBrowserStats();
        BeanUtils.copyProperties(linkBrowserStatsUpdateRequest, linkBrowserStats);
        // 数据校验
        linkBrowserStatsService.validLinkBrowserStats(linkBrowserStats, false);
        // 判断是否存在
        long id = linkBrowserStatsUpdateRequest.getId();
        LinkBrowserStats oldLinkBrowserStats = linkBrowserStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkBrowserStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkBrowserStatsService.updateById(linkBrowserStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接浏览器统计（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkBrowserStatsVO> getLinkBrowserStatsVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        LinkBrowserStats linkBrowserStats = linkBrowserStatsService.getById(id);
        ThrowUtils.throwIf(linkBrowserStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkBrowserStatsService.getLinkBrowserStatsVO(linkBrowserStats, request));
    }

    /**
     * 分页获取短链接浏览器统计列表（仅管理员可用）
     *
     * @param linkBrowserStatsQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<LinkBrowserStats>> listLinkBrowserStatsByPage(@RequestBody LinkBrowserStatsQueryRequest linkBrowserStatsQueryRequest) {
        long current = linkBrowserStatsQueryRequest.getCurrent();
        long size = linkBrowserStatsQueryRequest.getPageSize();
        // 查询数据库
        Page<LinkBrowserStats> linkBrowserStatsPage = linkBrowserStatsService.page(new Page<>(current, size),
                linkBrowserStatsService.getQueryWrapper(linkBrowserStatsQueryRequest));
        return ResultUtils.success(linkBrowserStatsPage);
    }

    /**
     * 分页获取短链接浏览器统计列表（封装类）
     *
     * @param linkBrowserStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkBrowserStatsVO>> listLinkBrowserStatsVOByPage(@RequestBody LinkBrowserStatsQueryRequest linkBrowserStatsQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkBrowserStatsQueryRequest.getCurrent();
        long size = linkBrowserStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkBrowserStats> linkBrowserStatsPage = linkBrowserStatsService.page(new Page<>(current, size),
                linkBrowserStatsService.getQueryWrapper(linkBrowserStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkBrowserStatsService.getLinkBrowserStatsVOPage(linkBrowserStatsPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接浏览器统计列表
     *
     * @param linkBrowserStatsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkBrowserStatsVO>> listMyLinkBrowserStatsVOByPage(@RequestBody LinkBrowserStatsQueryRequest linkBrowserStatsQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkBrowserStatsQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkBrowserStatsQueryRequest.setUserId(loginUser.getId());
        long current = linkBrowserStatsQueryRequest.getCurrent();
        long size = linkBrowserStatsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkBrowserStats> linkBrowserStatsPage = linkBrowserStatsService.page(new Page<>(current, size),
                linkBrowserStatsService.getQueryWrapper(linkBrowserStatsQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkBrowserStatsService.getLinkBrowserStatsVOPage(linkBrowserStatsPage, request));
    }

    /**
     * 编辑短链接浏览器统计（给用户使用）
     *
     * @param linkBrowserStatsEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLinkBrowserStats(@RequestBody LinkBrowserStatsEditRequest linkBrowserStatsEditRequest, HttpServletRequest request) {
        if (linkBrowserStatsEditRequest == null || linkBrowserStatsEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkBrowserStats linkBrowserStats = new LinkBrowserStats();
        BeanUtils.copyProperties(linkBrowserStatsEditRequest, linkBrowserStats);
        // 数据校验
        linkBrowserStatsService.validLinkBrowserStats(linkBrowserStats, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkBrowserStatsEditRequest.getId();
        LinkBrowserStats oldLinkBrowserStats = linkBrowserStatsService.getById(id);
        ThrowUtils.throwIf(oldLinkBrowserStats == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLinkBrowserStats);
        // 操作数据库
        boolean result = linkBrowserStatsService.updateById(linkBrowserStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(LinkBrowserStats linkBrowserStats)
    // {
    //     if (!linkBrowserStats.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
