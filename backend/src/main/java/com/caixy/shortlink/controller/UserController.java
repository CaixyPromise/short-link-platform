package com.caixy.shortlink.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.shortlink.annotation.AuthCheck;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.DeleteRequest;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.constant.RegexPatternConstants;
import com.caixy.shortlink.constant.UserConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.manager.authorization.AuthManager;
import com.caixy.shortlink.model.dto.user.*;
import com.caixy.shortlink.model.entity.User;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.user.AboutMeVO;
import com.caixy.shortlink.model.vo.user.AddUserVO;
import com.caixy.shortlink.model.vo.user.EncryptAccountVO;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.UserService;
import com.caixy.shortlink.utils.RegexUtils;
import com.caixy.shortlink.utils.ServletUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.caixy.shortlink.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController
{
    @Resource
    private UserService userService;

    @Resource
    private AuthManager authManager;

    // region 管理员增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<AddUserVO> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request)
    {
        // 检查请求信息
        if (userAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 参数校验
        String userAccount = user.getUserName();
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        // 生成默认密码
        String defaultPassword = userService.generatePassword();
        user.setUserPassword(defaultPassword);
        // 创建
        Long resultId = userService.doRegister(user);
        User newUser = userService.getById(resultId);
        // 返回结果
        AddUserVO resultAddUserInfo = AddUserVO.builder().userName(newUser.getNickName()).userAccount(newUser.getUserName()).userPassword(defaultPassword).id(resultId).build();
        return ResultUtils.success(resultAddUserInfo);
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request)
    {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        userService.validUserInfo(user, false);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<User> getUserById(long id, HttpServletRequest request)
    {
        if (id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    // endregion

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public Result<UserVO> getUserVOById(long id, HttpServletRequest request)
    {
        Result<User> response = getUserById(id, request);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request)
    {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size), userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request)
    {
        if (userQueryRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion


    @GetMapping("/get/me")
    public Result<AboutMeVO> getMe(HttpServletRequest request)
    {
        UserVO loginUser = authManager.getLoginUser();
        User currentUser = userService.getById(loginUser.getId());

        return ResultUtils.success(AboutMeVO.of(currentUser));
    }

    /**
     * 发送修改密码验证码邮件
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/26 3:45
     */
    @PostMapping("/modify/password/identification")
    public Result<Boolean> modifyPasswordStepByIdentification() {
        UserVO loginUser = authManager.getLoginUser();
        userService.sendModifyPasswordIdentifyCode(loginUser);
        return ResultUtils.success(true);
    }


    @PostMapping("/modify/password")
    public Result<Boolean> modifyPassword(@RequestBody @Valid UserModifyPasswordRequest userModifyPasswordRequest, HttpServletRequest request)
    {
        if (userModifyPasswordRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserVO loginUser = authManager.getLoginUser();
        Boolean result = userService.modifyPassword(loginUser.getId(), userModifyPasswordRequest);
        // 如果修改成功，修改登录状态
        if (result)
        {
            result = authManager.userLogout();
        }
        return ResultUtils.success(result);
    }

    /**
     * 更新个人信息
     *
     * @param userUpdateProfileRequest
     * @param request
     * @return
     */
    @PostMapping("/update/me")
    public Result<Boolean> updateMeProfile(@RequestBody UserUpdateProfileRequest userUpdateProfileRequest, HttpServletRequest request)
    {
        if (userUpdateProfileRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        User user = new User();
        BeanUtils.copyProperties(userUpdateProfileRequest, user);
        user.setId(loginUser.getId());
        userService.validUserInfo(user, false);
        boolean result = userService.updateUserAndSessionById(user, request);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/submit/reset/email/check/original")
    public Result<Boolean> submitResetEmail(@RequestBody UserResetEmailRequest userResetEmailRequest, HttpServletRequest request)
    {
        if (userResetEmailRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        userService.submitModifyEmailCheckOriginEmail(loginUser, userResetEmailRequest.getOriginalEmail());
        return ResultUtils.success(true);
    }

    @PostMapping("/submit/reset/email/check/identify")
    public Result<String> submitResetEmailIdentify(@RequestBody UserResetEmailRequest userResetEmailRequest)
    {
        if (userResetEmailRequest == null ||
            StringUtils.isAnyBlank(userResetEmailRequest.getPassword(), userResetEmailRequest.getCode())
        ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        return ResultUtils.success(userService.submitModifyEmailCheckPasswordAndCode(loginUser, userResetEmailRequest.getPassword(), userResetEmailRequest.getCode()));
    }

    @PostMapping("/submit/reset/email/check/valid")
    public Result<Boolean> submitResetEmailValidNewEmail(@RequestBody UserResetEmailRequest userResetEmailRequest)
    {
        if (userResetEmailRequest == null ||
                StringUtils.isAnyBlank(userResetEmailRequest.getToken(), userResetEmailRequest.getNewEmail())
        ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        userService.submitModifyEmailSendCodeToNewEmail(loginUser, userResetEmailRequest.getToken(), userResetEmailRequest.getNewEmail());
        return ResultUtils.success(true);
    }


    @PostMapping("/reset/email")
    public Result<Boolean> resetEmail(@RequestBody UserResetEmailRequest userResetEmailRequest)
    {
        if (userResetEmailRequest == null || StringUtils.isAnyBlank(userResetEmailRequest.getToken(), userResetEmailRequest.getCode()))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        Boolean result = userService.modifyEmail(loginUser.getId(), userResetEmailRequest.getToken(), userResetEmailRequest.getCode());
        return ResultUtils.success(result);
    }

    /**
     * 获取加密后的邮箱数据
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/14 下午10:16
     */
    @GetMapping("/get/encrypt/info")
    public Result<EncryptAccountVO> getEncryptEmailInfo(HttpServletRequest request)
    {
        UserVO loginUser = authManager.getLoginUser();
        String encryptedEmail = RegexUtils.encryptText(loginUser.getUserEmail(), RegexPatternConstants.EMAIL_ENCRYPT_REGEX, "$1****$2");
        String encryptedPhone = RegexUtils.encryptText(loginUser.getUserPhone(), RegexPatternConstants.PHONE_ENCRYPT_REGEX, "$1****$2");
        return ResultUtils.success(new EncryptAccountVO(encryptedEmail, encryptedPhone));

    }
}
