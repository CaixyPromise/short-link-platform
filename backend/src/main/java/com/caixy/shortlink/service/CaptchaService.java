package com.caixy.shortlink.service;

import com.caixy.shortlink.model.vo.captcha.CaptchaVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 验证码服务类
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.service.CaptchaService
 * @since 2024-07-16 04:08
 **/
public interface CaptchaService
{
    /**
     * 随机获取一个验证码服务类
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/7/16 上午4:09
     */
    CaptchaVO getAnyCaptcha(HttpServletRequest request);

    /**
     * 获取指定类型的验证码服务类
     */
    CaptchaVO getCaptchaByType(HttpServletRequest request, String type);

    boolean verifyCaptcha(String code, String captchaId);
}
