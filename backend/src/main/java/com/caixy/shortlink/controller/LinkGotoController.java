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
import com.caixy.shortlink.model.dto.linkGoto.LinkGotoAddRequest;
import com.caixy.shortlink.model.dto.linkGoto.LinkGotoEditRequest;
import com.caixy.shortlink.model.dto.linkGoto.LinkGotoQueryRequest;
import com.caixy.shortlink.model.dto.linkGoto.LinkGotoUpdateRequest;
import com.caixy.shortlink.model.entity.LinkGoto;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkGoto.LinkGotoVO;
import com.caixy.shortlink.service.LinkGotoService;
import com.caixy.shortlink.manager.Authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接跳转信息接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/linkGoto")
public class LinkGotoController {

    private final LinkGotoService linkGotoService;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建短链接跳转信息
     *
     * @param linkGotoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addLinkGoto(@RequestBody LinkGotoAddRequest linkGotoAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(linkGotoAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        LinkGoto linkGoto = new LinkGoto();
        BeanUtils.copyProperties(linkGotoAddRequest, linkGoto);
        // 数据校验
        linkGotoService.validLinkGoto(linkGoto, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // linkGoto.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = linkGotoService.save(linkGoto);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newLinkGotoId = linkGoto.getId();
        return ResultUtils.success(newLinkGotoId);
    }

    /**
     * 删除短链接跳转信息
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteLinkGoto(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        LinkGoto oldLinkGoto = linkGotoService.getById(id);
        ThrowUtils.throwIf(oldLinkGoto == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(oldLinkGoto);
        // 操作数据库
        boolean result = linkGotoService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新短链接跳转信息（仅管理员可用）
     *
     * @param linkGotoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateLinkGoto(@RequestBody LinkGotoUpdateRequest linkGotoUpdateRequest) {
        if (linkGotoUpdateRequest == null || linkGotoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkGoto linkGoto = new LinkGoto();
        BeanUtils.copyProperties(linkGotoUpdateRequest, linkGoto);
        // 数据校验
        linkGotoService.validLinkGoto(linkGoto, false);
        // 判断是否存在
        long id = linkGotoUpdateRequest.getId();
        LinkGoto oldLinkGoto = linkGotoService.getById(id);
        ThrowUtils.throwIf(oldLinkGoto == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = linkGotoService.updateById(linkGoto);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取短链接跳转信息（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<LinkGotoVO> getLinkGotoVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        LinkGoto linkGoto = linkGotoService.getById(id);
        ThrowUtils.throwIf(linkGoto == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(linkGotoService.getLinkGotoVO(linkGoto, request));
    }

    /**
     * 分页获取短链接跳转信息列表（仅管理员可用）
     *
     * @param linkGotoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<LinkGoto>> listLinkGotoByPage(@RequestBody LinkGotoQueryRequest linkGotoQueryRequest) {
        long current = linkGotoQueryRequest.getCurrent();
        long size = linkGotoQueryRequest.getPageSize();
        // 查询数据库
        Page<LinkGoto> linkGotoPage = linkGotoService.page(new Page<>(current, size),
                linkGotoService.getQueryWrapper(linkGotoQueryRequest));
        return ResultUtils.success(linkGotoPage);
    }

    /**
     * 分页获取短链接跳转信息列表（封装类）
     *
     * @param linkGotoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<LinkGotoVO>> listLinkGotoVOByPage(@RequestBody LinkGotoQueryRequest linkGotoQueryRequest,
                                                               HttpServletRequest request) {
        long current = linkGotoQueryRequest.getCurrent();
        long size = linkGotoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkGoto> linkGotoPage = linkGotoService.page(new Page<>(current, size),
                linkGotoService.getQueryWrapper(linkGotoQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkGotoService.getLinkGotoVOPage(linkGotoPage, request));
    }

    /**
     * 分页获取当前登录用户创建的短链接跳转信息列表
     *
     * @param linkGotoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<LinkGotoVO>> listMyLinkGotoVOByPage(@RequestBody LinkGotoQueryRequest linkGotoQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(linkGotoQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        linkGotoQueryRequest.setUserId(loginUser.getId());
        long current = linkGotoQueryRequest.getCurrent();
        long size = linkGotoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<LinkGoto> linkGotoPage = linkGotoService.page(new Page<>(current, size),
                linkGotoService.getQueryWrapper(linkGotoQueryRequest));
        // 获取封装类
        return ResultUtils.success(linkGotoService.getLinkGotoVOPage(linkGotoPage, request));
    }

    /**
     * 编辑短链接跳转信息（给用户使用）
     *
     * @param linkGotoEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editLinkGoto(@RequestBody LinkGotoEditRequest linkGotoEditRequest, HttpServletRequest request) {
        if (linkGotoEditRequest == null || linkGotoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        LinkGoto linkGoto = new LinkGoto();
        BeanUtils.copyProperties(linkGotoEditRequest, linkGoto);
        // 数据校验
        linkGotoService.validLinkGoto(linkGoto, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = linkGotoEditRequest.getId();
        LinkGoto oldLinkGoto = linkGotoService.getById(id);
        ThrowUtils.throwIf(oldLinkGoto == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(oldLinkGoto);
        // 操作数据库
        boolean result = linkGotoService.updateById(linkGoto);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(LinkGoto linkGoto)
    // {
    //     if (!linkGoto.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
