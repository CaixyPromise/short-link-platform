package com.caixy.shortlink.model.enums;

import lombok.Getter;
import com.caixy.shortlink.utils.StringUtils;
import org.redisson.api.RateIntervalUnit;

/**
 * Redis限流器枚举
 * <p>
 * 设置限流器的规则。这里设定的规则是每秒最多允许total个请求
 * * 每个请求间隔rate个时间单位
 * * 获取1个权限，也就是每秒最多处理2个请求
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.model.enums.RedisLimiterEnum
 * @since 2024-07-16 21:46
 **/
@Getter
public enum RedisLimiterEnum
{
    LOGIN("login:rate-limiter", 1, 1, RateIntervalUnit.SECONDS),  // 每秒最多1次请求
    CAPTCHA("captcha-rate-limiter", 3, 1, RateIntervalUnit.SECONDS),  // 每秒最多3次请求
    REGISTER("register-rate-limiter", 1, 1, RateIntervalUnit.SECONDS), // 每秒最多1次请求
    // 注册信息  1秒最多2次
    REGISTER_INFO("register-info-rate-limiter", 2, 1, RateIntervalUnit.SECONDS),
    ;

    /**
     * 限流名称
     */
    private final String key;
    /**
     * rate：在指定的时间间隔内允许的请求数或令牌数。
     */
    private final int rate;
    /**
     * rateInterval：与 intervalUnit 配合使用，定义时间间隔的大小。
     */
    private final int rateInterval;

    /**
     * 时间单位
     */
    private final RateIntervalUnit rateIntervalUnit;

    RedisLimiterEnum(String key, int rate, int rateInterval, RateIntervalUnit rateIntervalUnit)
    {
        this.key = key.endsWith(":") ? key : key + ":";
        this.rate = rate;
        this.rateInterval = rateInterval;
        this.rateIntervalUnit = rateIntervalUnit;
    }

    public String generateKey(String... items)
    {
        if (items == null || items.length == 0)
        {
            return key;
        }
        return key.concat(String.join(":", items));
    }
}
