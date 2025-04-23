package com.caixy.shortlink.controller;

import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.config.WxOpenConfig;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.oauth.factory.OAuthFactory;
import com.caixy.shortlink.manager.authorization.AuthManager;
import com.caixy.shortlink.manager.limiter.annotation.RateLimitFlow;
import com.caixy.shortlink.model.dto.oauth.OAuthResultResponse;
import com.caixy.shortlink.model.dto.oauth.github.GithubGetAuthorizationUrlRequest;
import com.caixy.shortlink.model.dto.user.UserActivationRequest;
import com.caixy.shortlink.model.dto.user.UserLoginRequest;
import com.caixy.shortlink.model.dto.user.UserRegisterRequest;
import com.caixy.shortlink.model.enums.OAuthProviderEnum;
import com.caixy.shortlink.model.enums.RedisLimiterEnum;
import com.caixy.shortlink.model.vo.user.LoginUserVO;
import com.caixy.shortlink.model.vo.user.RegistrationInfo;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.CaptchaService;
import com.caixy.shortlink.service.EmailService;
import com.caixy.shortlink.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import com.caixy.shortlink.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 授权接口控制器
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.controller.AuthController
 * @since 2024/10/17 18:04
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController
{
    private final AuthManager authManager;

    private final WxOpenConfig wxOpenConfig;

    private final OAuthFactory oAuthFactory;

    private final UserService userService;

    private final CaptchaService captchaService;

    private final EmailService emailService;


    @GetMapping("/oauth2/{provider}/login")
    public Result<String> initOAuthLogin(@PathVariable String provider, @ModelAttribute GithubGetAuthorizationUrlRequest authorizationUrlRequest, HttpServletRequest request)
    {
        OAuthProviderEnum providerEnum = OAuthProviderEnum.getProviderEnum(provider);
        if (providerEnum == null)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持的OAuth2登录方式");
        }
        authorizationUrlRequest.setSessionId(request.getSession().getId());
        log.info("authorizationUrlRequest:{}", authorizationUrlRequest);
        String authorizationUrl = oAuthFactory.getOAuth2ActionStrategy(providerEnum).getAuthorizationUrl(authorizationUrlRequest);
        return ResultUtils.success(authorizationUrl);
    }

    @GetMapping("/oauth2/{provider}/callback")
    public void oAuthLoginCallback(@PathVariable("provider") String provider, @RequestParam Map<String, Object> allParams, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        allParams.put("sessionId", request.getSession().getId());
        try
        {
            OAuthProviderEnum providerEnum = OAuthProviderEnum.getProviderEnum(provider);
            if (providerEnum == null)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持的OAuth2登录方式");
            }
            OAuthResultResponse oAuthResultResponse = oAuthFactory.doAuth(providerEnum, allParams);
            Boolean doOAuthLogin = authManager.doOAuthLogin(oAuthResultResponse, providerEnum);
            if (doOAuthLogin)
            {
                response.sendRedirect(oAuthResultResponse.getRedirectUrl());
            }
            else
            {
                response.sendRedirect(CommonConstant.FRONTED_URL);
            }
        }
        catch (Exception e)
        {
            response.sendRedirect(CommonConstant.FRONTED_URL);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    /**
     * 用户注销
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<Boolean> userLogout()
    {
        boolean result = authManager.userLogout();
        return ResultUtils.success(result);
    }


    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest)
    {
        if (userLoginRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(authManager.userLogin(userLoginRequest));
    }

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public Result<Boolean> userRegister(@RequestBody UserRegisterRequest userRegisterRequest)
    {
        if (userRegisterRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 检查图像验证码
        boolean verifyCaptcha = captchaService.verifyCaptcha(userRegisterRequest.getCaptcha(), userRegisterRequest.getCaptchaId());
        if (!verifyCaptcha) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        userService.userPreRegistration(userRegisterRequest);
        return ResultUtils.success(true);
    }

    @PostMapping("/activate/{token}/{code}")
    public Result<Boolean> doActivateUser(@PathVariable("token") String token, @PathVariable("code") String code,
                                          @RequestBody UserActivationRequest userActivationRequest) {
        if (StringUtils.isAnyBlank(token, code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效请求");
        }
        return ResultUtils.success(userService.doActivateUser(token, code, userActivationRequest));
    }




    @GetMapping("/get/registration_info")
//    @RateLimitFlow(key = RedisLimiterEnum.REGISTER_INFO, args = "#request.getRemoteAddr()")
    public Result<RegistrationInfo> getRegistrationInfoByParams(@RequestParam("token") String token, HttpServletRequest request) {
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效请求");
        }
        return ResultUtils.success(userService.getRegistrationInfoByParams(token));
    }



    @GetMapping("/get/login")
    public Result<LoginUserVO> getLoginUser()
    {
        UserVO user = authManager.getLoginUser();
        return ResultUtils.success(authManager.getLoginUserVO(user));
    }

    @GetMapping("/login/wx_open")
    public Result<LoginUserVO> userLoginByWxOpen(@RequestParam("code") String code)
    {
        WxOAuth2AccessToken accessToken;
        try
        {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId))
            {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            return ResultUtils.success(authManager.userLoginByMpOpen(userInfo));
        }
        catch (Exception e)
        {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }
}
