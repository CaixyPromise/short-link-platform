package com.caixy.shortlink.service.impl;

import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.captcha.factory.CaptchaFactory;
import com.caixy.shortlink.model.vo.captcha.CaptchaVO;
import com.caixy.shortlink.service.CaptchaService;
import com.caixy.shortlink.manager.captcha.strategy.CaptchaGenerationStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 验证码服务类接口实现类
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.service.impl.CaptchaServiceImpl
 * @since 2024-07-16 04:09
 **/
@Service
@AllArgsConstructor
public class CaptchaServiceImpl implements CaptchaService
{
    private final CaptchaFactory captchaFactory;

    @Override
    public CaptchaVO getAnyCaptcha(HttpServletRequest request)
    {
        CaptchaGenerationStrategy randomCaptchaStrategy = captchaFactory.getRandomCaptchaStrategy();
        return randomCaptchaStrategy.generateCaptcha(request);
    }

    @Override
    public CaptchaVO getCaptchaByType(HttpServletRequest request, String type)
    {
        CaptchaGenerationStrategy captchaStrategy = captchaFactory.getCaptchaStrategy(type);
        if (captchaStrategy == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码类型错误");
        }
        return captchaStrategy.generateCaptcha(request);
    }

    /**
     * 校验验证码
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/7/18 上午1:54
     */
    @Override
    public boolean verifyCaptcha(String code, String captchaId)
    {
        return !captchaFactory.verifyCaptcha(code, captchaId);
    }
}
