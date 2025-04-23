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
import com.caixy.shortlink.model.dto.linkStatsToday.LinkStatsTodayAddRequest;
import com.caixy.shortlink.model.dto.linkStatsToday.LinkStatsTodayEditRequest;
import com.caixy.shortlink.model.dto.linkStatsToday.LinkStatsTodayQueryRequest;
import com.caixy.shortlink.model.dto.linkStatsToday.LinkStatsTodayUpdateRequest;
import com.caixy.shortlink.model.entity.LinkStatsToday;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkStatsToday.LinkStatsTodayVO;
import com.caixy.shortlink.service.LinkStatsTodayService;
import com.caixy.shortlink.manager.authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接当日统计接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/linkStatsToday")
public class LinkStatsTodayController {

    private final LinkStatsTodayService linkStatsTodayService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接当日统计
     *
     * @param linkStatsTodayAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLinkStatsToday(@RequestBody LinkStatsTodayAddRequest linkStatsTodayAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkStatsTodayAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        LinkStatsToday linkStatsToday = new LinkStatsToday();
        BeanUtils.copyProperties(linkStatsTodayAddRequest, linkStatsToday);
        // 数据校验
        linkStatsTodayService.validLinkStatsToday(linkStatsToday, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // linkStatsToday.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkStatsTodayService.save(linkStatsToday);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkStatsTodayId = linkStatsToday.getId();
        return ResultUtils.success(newLinkStatsTodayId);
    }

    /**
     * 删除短链接当日统计
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLinkStatsToday(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        LinkStatsToday oldLinkStatsToday = linkStatsTodayService.getById(id);
        ThrowUtils.throwIf(oldLinkStatsToday == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLinkStatsToday);
        // 操作数据库
        boolean result = linkStatsTodayService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接当日统计（仅管理员可用）
     *
     * @param linkStatsTodayUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLinkStatsToday(@RequestBody LinkStatsTodayUpdateRequest linkStatsTodayUpdateRequest) {
        if (linkStatsTodayUpdateRequest == null || linkStatsTodayUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkStatsToday linkStatsToday = new LinkStatsToday();
        BeanUtils.copyProperties(linkStatsTodayUpdateRequest, linkStatsToday);
        // 数据校验
        linkStatsTodayService.validLinkStatsToday(linkStatsToday, false);
        // 判断是否存在
        long id = linkStatsTodayUpdateRequest.getId();
        LinkStatsToday oldLinkStatsToday = linkStatsTodayService.getById(id);
        ThrowUtils.throwIf(oldLinkStatsToday == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkStatsTodayService.updateById(linkStatsToday);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接当日统计（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkStatsTodayVO> getLinkStatsTodayVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        LinkStatsToday linkStatsToday = linkStatsTodayService.getById(id);
        ThrowUtils.throwIf(linkStatsToday == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkStatsTodayService.getLinkStatsTodayVO(linkStatsToday, request));
    }

    /**
     * 分页获取短链接当日统计列表（仅管理员可用）
     *
     * @param linkStatsTodayQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<LinkStatsToday>> listLinkStatsTodayByPage(@RequestBody LinkStatsTodayQueryRequest linkStatsTodayQueryRequest) {
        long current = linkStatsTodayQueryRequest.getCurrent();
        long size = linkStatsTodayQueryRequest.getPageSize();
        // 查询数据库
        Page<LinkStatsToday> linkStatsTodayPage = linkStatsTodayService.page(new Page<>(current, size),
                linkStatsTodayService.getQueryWrapper(linkStatsTodayQueryRequest));
        return ResultUtils.success(linkStatsTodayPage);
    }

    /**
     * 分页获取短链接当日统计列表（封装类）
     *
     * @param linkStatsTodayQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkStatsTodayVO>> listLinkStatsTodayVOByPage(@RequestBody LinkStatsTodayQueryRequest linkStatsTodayQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkStatsTodayQueryRequest.getCurrent();
        long size = linkStatsTodayQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkStatsToday> linkStatsTodayPage = linkStatsTodayService.page(new Page<>(current, size),
                linkStatsTodayService.getQueryWrapper(linkStatsTodayQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkStatsTodayService.getLinkStatsTodayVOPage(linkStatsTodayPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接当日统计列表
     *
     * @param linkStatsTodayQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkStatsTodayVO>> listMyLinkStatsTodayVOByPage(@RequestBody LinkStatsTodayQueryRequest linkStatsTodayQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkStatsTodayQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkStatsTodayQueryRequest.setUserId(loginUser.getId());
        long current = linkStatsTodayQueryRequest.getCurrent();
        long size = linkStatsTodayQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkStatsToday> linkStatsTodayPage = linkStatsTodayService.page(new Page<>(current, size),
                linkStatsTodayService.getQueryWrapper(linkStatsTodayQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkStatsTodayService.getLinkStatsTodayVOPage(linkStatsTodayPage, request));
    }

    /**
     * 编辑短链接当日统计（给用户使用）
     *
     * @param linkStatsTodayEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLinkStatsToday(@RequestBody LinkStatsTodayEditRequest linkStatsTodayEditRequest, HttpServletRequest request) {
        if (linkStatsTodayEditRequest == null || linkStatsTodayEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkStatsToday linkStatsToday = new LinkStatsToday();
        BeanUtils.copyProperties(linkStatsTodayEditRequest, linkStatsToday);
        // 数据校验
        linkStatsTodayService.validLinkStatsToday(linkStatsToday, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkStatsTodayEditRequest.getId();
        LinkStatsToday oldLinkStatsToday = linkStatsTodayService.getById(id);
        ThrowUtils.throwIf(oldLinkStatsToday == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLinkStatsToday);
        // 操作数据库
        boolean result = linkStatsTodayService.updateById(linkStatsToday);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(LinkStatsToday linkStatsToday)
    // {
    //     if (!linkStatsToday.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
