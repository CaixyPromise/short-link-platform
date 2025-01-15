package com.caixy.shortlink.aop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.manager.ThreadPoolManager.AsyncManager;
import com.caixy.shortlink.manager.ThreadPoolManager.factory.AsyncTaskFactory;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.mapper.ApiKeyMapper;
import com.caixy.shortlink.model.entity.ApiKey;
import com.caixy.shortlink.model.enums.RedisKeyEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.TimerTask;

@Component
@RequiredArgsConstructor
public class SDKRequestInterceptor implements HandlerInterceptor
{

    private static final Logger log = LoggerFactory.getLogger(SDKRequestInterceptor.class);
    private final ApiKeyMapper apiKeyMapper;
    private final RedisManager redisManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        String accessKey = request.getHeader("X-Access-Key");
        String signature = request.getHeader("X-Signature");
        String timestamp = request.getHeader("X-Timestamp");
        String nonce = request.getHeader("X-Nonce");

        if (accessKey == null || signature == null || timestamp == null || nonce == null)
        {
            log.info("缺少请求头参数: accessKey={}, signature={}, timestamp={}, nonce={}", accessKey, signature,
                    timestamp, nonce);
            respondUnauthorized(response, ResultUtils.error(ErrorCode.PARAMS_ERROR, "缺少请求头参数"));
            return false;
        }

        long currentTime = System.currentTimeMillis();
        try
        {
            long requestTime = Long.parseLong(timestamp);
            if (Math.abs(currentTime - requestTime) > 5 * 60 * 1000)
            {
                log.info("请求时间戳无效: {}", timestamp);
                respondUnauthorized(response, ResultUtils.error(ErrorCode.PARAMS_ERROR, "请求时间戳无效"));
                return false;
            }
        }
        catch (NumberFormatException e)
        {
            log.info("无效的请求时间戳: {}", timestamp);
            respondUnauthorized(response, ResultUtils.error(ErrorCode.PARAMS_ERROR, "无效的请求时间戳"));
            return false;
        }

        if (!redisManager.setIfAbsent(RedisKeyEnum.SDK_NONCE_KEY, accessKey, nonce))
        {
            log.info("无效的请求nonce: {}", nonce);

            respondUnauthorized(response, ResultUtils.error(ErrorCode.PARAMS_ERROR, "无效的请求nonce"));
            return false;
        }

        ApiKey useApiKey = apiKeyMapper.selectSecretKeyByAccessKey(accessKey);
        if (useApiKey == null)
        {
            log.info("无效的accessKey: {}", accessKey);
            respondUnauthorized(response, ResultUtils.error(ErrorCode.PARAMS_ERROR, "无效的accessKey"));
            return false;
        }
        String secretKey = useApiKey.getSecretKey();

        if (!verifySignature(signature, accessKey, timestamp, nonce, secretKey))
        {
            log.info("无效的请求签名: {}", signature);
            respondUnauthorized(response, ResultUtils.error(ErrorCode.PARAMS_ERROR, "无效的请求签名"));
            return false;
        }

        UserVO userVO = apiKeyMapper.selectUserVOByAccessKey(accessKey);
        if (userVO == null)
        {
            log.info("无用户信息: {}", accessKey);
            respondUnauthorized(response, ResultUtils.error(ErrorCode.PARAMS_ERROR, "无用户信息"));
        }
        request.setAttribute(CommonConstant.SDK_USER_KEY, userVO);

        return true;
    }

    private void respondUnauthorized(HttpServletResponse response, Result<String> result) throws IOException
    {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(JsonUtils.toJsonString(result));
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().flush();
    }


    private boolean verifySignature(String signature, String accessKey, String timestamp, String nonce,
                                    String secretKey)
    {
        try
        {
            String data = accessKey + timestamp + nonce;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            String expectedSignature = Base64.getEncoder().encodeToString(hash);
            return expectedSignature.equals(signature);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    {
        UserVO userVO = (UserVO) request.getAttribute(CommonConstant.SDK_USER_KEY);
        AsyncManager.me().execute(() -> updateLashRequestByAsync(userVO));
        request.removeAttribute(CommonConstant.SDK_USER_KEY);
        log.info("调用SDK用户Id: {}, 接口: {}", userVO.getId(), request.getServletPath());
    }

    private void updateLashRequestByAsync(UserVO userVO)
    {
        LambdaUpdateWrapper<ApiKey> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ApiKey::getUserId, userVO.getId())
                     .set(ApiKey::getLastRequestTime, new Date());
        apiKeyMapper.update(null, updateWrapper);
    }
}
