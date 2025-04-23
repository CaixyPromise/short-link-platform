package com.caixy.shortlink.manager.captcha.strategy;

import cn.hutool.core.codec.Base64;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.model.enums.RedisKeyEnum;
import com.caixy.shortlink.model.vo.captcha.CaptchaVO;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.utils.ServletUtils;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import org.springframework.util.FastByteArrayOutputStream;

import jakarta.annotation.PostConstruct;

import jakarta.servlet.http.HttpServletRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * 验证码生成抽象类
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.captcha.strategy.CaptchaGenerationStrategy
 * @since 2024-07-15 19:43
 **/
public abstract class CaptchaGenerationStrategy
{
    @Resource
    private RedisManager redisManager;

    protected Producer captchaProducer;

    @PostConstruct
    public void init()
    {
        captchaProducer = makeProducer();
    }

    public abstract CaptchaVO generateCaptcha(HttpServletRequest request);

    protected abstract Producer makeProducer(); // 确保这个方法只能被继承者使用

    protected CaptchaVO saveResult(String code, BufferedImage image, HttpServletRequest request)
    {
        HashMap<String, String> resultMap = new HashMap<>();
        String uuid = UUID.randomUUID().toString();

        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", outputStream);
        }
        catch (IOException e)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        resultMap.put("uuid", uuid);
        resultMap.put("code", code);
        // 写入redis
        // 以uuid作为凭证，
        // 并设置过期时间: 5分钟
        redisManager.setHashMap(RedisKeyEnum.CAPTCHA_CODE,
                resultMap, ServletUtils.getSessionId());
        // 过期时间5分钟
        // 返回Base64的验证码图片信息
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCodeImage(Base64.encode(outputStream.toByteArray()));
        captchaVO.setUuid(uuid);
        request.getSession().setAttribute(CommonConstant.CAPTCHA_SIGN, uuid);
        return captchaVO;
    }

    protected void tryRemoveLastCaptcha(HttpServletRequest request)
    {
        Object lastUuid = request.getSession().getAttribute(CommonConstant.CAPTCHA_SIGN);
        if (lastUuid != null)
        {
            redisManager.delete(RedisKeyEnum.CAPTCHA_CODE, request.getRequestedSessionId());
        }
    }
}
