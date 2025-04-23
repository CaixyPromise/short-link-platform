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
import com.caixy.shortlink.model.dto.link.LinkAddRequest;
import com.caixy.shortlink.model.dto.link.LinkEditRequest;
import com.caixy.shortlink.model.dto.link.LinkQueryRequest;
import com.caixy.shortlink.model.dto.link.LinkUpdateRequest;
import com.caixy.shortlink.model.entity.Link;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.link.LinkVO;
import com.caixy.shortlink.service.LinkService;
import com.caixy.shortlink.manager.authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接信息接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/link")
public class LinkController {

    private final LinkService linkService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接信息
     *
     * @param linkAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLink(@RequestBody LinkAddRequest linkAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Link link = new Link();
        BeanUtils.copyProperties(linkAddRequest, link);
        // 数据校验
        linkService.validLink(link, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // link.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkService.save(link);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkId = link.getId();
        return ResultUtils.success(newLinkId);
    }

    /**
     * 删除短链接信息
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLink(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        Link oldLink = linkService.getById(id);
        ThrowUtils.throwIf(oldLink == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLink);
        // 操作数据库
        boolean result = linkService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接信息（仅管理员可用）
     *
     * @param linkUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLink(@RequestBody LinkUpdateRequest linkUpdateRequest) {
        if (linkUpdateRequest == null || linkUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Link link = new Link();
        BeanUtils.copyProperties(linkUpdateRequest, link);
        // 数据校验
        linkService.validLink(link, false);
        // 判断是否存在
        long id = linkUpdateRequest.getId();
        Link oldLink = linkService.getById(id);
        ThrowUtils.throwIf(oldLink == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkService.updateById(link);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接信息（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkVO> getLinkVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Link link = linkService.getById(id);
        ThrowUtils.throwIf(link == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkService.getLinkVO(link, request));
    }

    /**
     * 分页获取短链接信息列表（仅管理员可用）
     *
     * @param linkQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<Link>> listLinkByPage(@RequestBody LinkQueryRequest linkQueryRequest) {
        long current = linkQueryRequest.getCurrent();
        long size = linkQueryRequest.getPageSize();
        // 查询数据库
        Page<Link> linkPage = linkService.page(new Page<>(current, size),
                linkService.getQueryWrapper(linkQueryRequest));
        return ResultUtils.success(linkPage);
    }

    /**
     * 分页获取短链接信息列表（封装类）
     *
     * @param linkQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkVO>> listLinkVOByPage(@RequestBody LinkQueryRequest linkQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkQueryRequest.getCurrent();
        long size = linkQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Link> linkPage = linkService.page(new Page<>(current, size),
                linkService.getQueryWrapper(linkQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkService.getLinkVOPage(linkPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接信息列表
     *
     * @param linkQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkVO>> listMyLinkVOByPage(@RequestBody LinkQueryRequest linkQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkQueryRequest.setUserId(loginUser.getId());
        long current = linkQueryRequest.getCurrent();
        long size = linkQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Link> linkPage = linkService.page(new Page<>(current, size),
                linkService.getQueryWrapper(linkQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkService.getLinkVOPage(linkPage, request));
    }

    /**
     * 编辑短链接信息（给用户使用）
     *
     * @param linkEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLink(@RequestBody LinkEditRequest linkEditRequest, HttpServletRequest request) {
        if (linkEditRequest == null || linkEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Link link = new Link();
        BeanUtils.copyProperties(linkEditRequest, link);
        // 数据校验
        linkService.validLink(link, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkEditRequest.getId();
        Link oldLink = linkService.getById(id);
        ThrowUtils.throwIf(oldLink == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLink);
        // 操作数据库
        boolean result = linkService.updateById(link);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(Link link)
    // {
    //     if (!link.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
