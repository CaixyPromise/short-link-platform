package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.user.*;
import com.caixy.shortlink.model.entity.User;
import com.caixy.shortlink.model.vo.user.RegistrationInfo;
import com.caixy.shortlink.model.vo.user.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 */
public interface UserService extends IService<User>
{

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    //    Long makeRegister(String userAccount, String userPassword);
    Long doRegister(User user);

    String generatePassword();

    void sendModifyPasswordIdentifyCode(UserVO userVO);

    Boolean modifyPassword(Long userId, UserModifyPasswordRequest userModifyPasswordRequest);

    void validUserInfo(User user, boolean add);

    User getUserInfoByIdOrThrow(Long userId);

    Boolean updateUserAndSessionById(User user, HttpServletRequest request);

    void submitModifyEmailCheckOriginEmail(UserVO userInfo, String originEmail);

    String submitModifyEmailCheckPasswordAndCode(UserVO loginUser, String password, String code);

    void submitModifyEmailSendCodeToNewEmail(UserVO loginUser, String token, String newEmail);

    Boolean modifyEmail(Long id, String token, String code);

    Boolean userPreRegistration(UserRegisterRequest userRegisterRequest);

    RegistrationInfo getRegistrationInfoByParams(String token);

    Boolean doActivateUser(String token, String code, UserActivationRequest userActivationRequest);
}
