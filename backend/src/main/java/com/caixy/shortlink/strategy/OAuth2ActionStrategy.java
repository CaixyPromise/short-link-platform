package com.caixy.shortlink.strategy;

import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.model.dto.oauth.OAuthResultResponse;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;


import java.util.Map;

/**
 * OAuth2验证类
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.strategy.OAuth2ActionStrategy
 * @since 2024/8/2 下午3:09
 */
public abstract class OAuth2ActionStrategy<
        CallbackPayloadResponseType, // 回调参数类型
        UserProfileType,    // 用户信息类型
        GetAuthorizationUrlRequestType, // 获取授权URL类型
        GetCallbackRequestType>    // 获取回调请求类型
{
    @Resource
    protected RedisManager redisManager;

    public abstract String getAuthorizationUrl(GetAuthorizationUrlRequestType authorizationUrlType);

    public abstract CallbackPayloadResponseType doCallback(GetCallbackRequestType callbackType);

    public abstract UserProfileType getUserProfile(CallbackPayloadResponseType callback);

    public abstract OAuthResultResponse doAuth(Map<String, Object> paramMaps);

    protected <T> T safetyConvertMapToObject(Map<String, Object> paramMaps, Class<T> clazz)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        T convertValue = objectMapper.convertValue(paramMaps, clazz);
        if (convertValue == null)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "授权失败");
        }
        return convertValue;
    }
}