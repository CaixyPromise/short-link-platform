package com.caixy.shortlink.controller;

import com.caixy.shortlink.manager.Limiter.annotation.RateLimitFlow;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.model.enums.RedisLimiterEnum;
import com.caixy.shortlink.model.vo.captcha.CaptchaVO;
import com.caixy.shortlink.service.CaptchaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 验证码接口控制器
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.controller.CaptchaController
 * @since 2024-07-16 03:59
 **/
@Slf4j
@RestController
@RequestMapping("/captcha")
public class CaptchaController
{
    @Resource
    private CaptchaService captchaService;

    @GetMapping("/get")
    @RateLimitFlow(key = RedisLimiterEnum.CAPTCHA, args = "#request.getSession().getId()")
    public Result<CaptchaVO> getCaptcha(HttpServletRequest request)
    {
        return ResultUtils.success(captchaService.getAnyCaptcha(request));
    }
}
