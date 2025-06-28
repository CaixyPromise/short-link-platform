package com.caixy.shortlink.manager.captcha.factory;

import cn.hutool.core.util.RandomUtil;
import com.caixy.shortlink.manager.captcha.annotation.CaptchaTypeTarget;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.model.enums.RedisKeyEnum;
import com.caixy.shortlink.manager.captcha.strategy.CaptchaGenerationStrategy;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.utils.ServletUtils;
import com.caixy.shortlink.utils.SpringContextUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码生成工厂
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.captcha.factory.CaptchaFactory
 * @since 2024-07-16 03:33
 **/
@Component
@Slf4j
public class CaptchaFactory
{
    @Resource
    private List<CaptchaGenerationStrategy> captchaGenerationStrategies;

    @Resource
    private RedisManager redisManager;

    private ConcurrentHashMap<String, CaptchaGenerationStrategy> serviceCache;

    private List<CaptchaGenerationStrategy> registeredStrategies;

    @PostConstruct
    public void initActionService()
    {
        serviceCache =
                SpringContextUtils.getServiceFromAnnotation(captchaGenerationStrategies, CaptchaTypeTarget.class, "value");
        registeredStrategies = new ArrayList<>(serviceCache.values());
    }

    public CaptchaGenerationStrategy getCaptchaStrategy(String type)
    {
        return serviceCache.get(type);
    }

    public CaptchaGenerationStrategy getRandomCaptchaStrategy()
    {
        return RandomUtil.randomEle(registeredStrategies);
    }

    public boolean verifyCaptcha(String captchaCode, String captchaId)
    {
        // 获取SessionId
        String sessionId = ServletUtils.getSessionId();
        String sessionUuid = Optional.ofNullable(ServletUtils.getAttributeFromSessionOrNull(CommonConstant.CAPTCHA_SIGN, String.class))
                                     .orElseThrow(()->{
                                         log.error("验证码校验失败，session中不存在验证码标识，sessionId:{}", sessionId);
                                         return new BusinessException(ErrorCode.OPERATION_ERROR, "验证码校验失败");
                                     });
        // 1.2 校验验证码
        Map<String, Object> result = redisManager.getHashMap(
                RedisKeyEnum.CAPTCHA_CODE,
                sessionId);
        if (sessionUuid == null || result == null || result.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        String redisCode = Optional.ofNullable(result.get("code")).orElseThrow(() -> new BusinessException(ErrorCode.OPERATION_ERROR)).toString().trim();
        String redisUuid = Optional.ofNullable(result.get("uuid")).orElseThrow(() -> new BusinessException(ErrorCode.OPERATION_ERROR)).toString().trim();
        // 移除session缓存的uuid
        ServletUtils.removeAttributeInSession(CommonConstant.CAPTCHA_SIGN);
        boolean removeByCache = redisManager.delete(RedisKeyEnum.CAPTCHA_CODE, sessionId);
        if (!removeByCache)
        {
            log.warn("验证码校验失败，移除缓存失败，sessionId:{}", sessionId);
        }
        // 验证码不区分大小写，同时校验前后的session内的uuid是否一致。
        return !redisCode.equalsIgnoreCase(captchaCode.trim()) && sessionUuid.equals(captchaId) && captchaId.equals(redisUuid);
    }
}
