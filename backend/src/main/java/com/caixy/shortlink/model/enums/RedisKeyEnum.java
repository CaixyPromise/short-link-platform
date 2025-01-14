package com.caixy.shortlink.model.enums;

import com.caixy.shortlink.common.BaseCacheEnum;
import com.caixy.shortlink.utils.TimeUtils;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum RedisKeyEnum implements BaseCacheEnum
{

    CATEGORY_PARENT_BY_KEY("category:parent:", -1L),

    /**
     * 验证码缓存，5分钟
     */
    CAPTCHA_CODE("captcha:", 60L * 5),

    /**
     * github OAuth验证信息缓存，5分钟
     */
    GITHUB_OAUTH("github_oauth:", 60L * 5),

    RESET_PASSWORD("reset_psw", 60L * 5),
    
    /**
    * Redis中存储所有活跃tokenId的集合key
    */
    TOKEN_ACTIVE_SET("token:activeTokens", -1L),

    /**
    * redis中存储用户与tokenId映射的key前缀
    */
    TOKEN_USER_KEY_PREFIX("token:user:", -1L),

    /**
    * 跳转链接缓存
    */
    TARGET_SHORT_LINK("target_short_link:", -1L),

    /**
    * 无效短链接缓存，持续一天
    */
    INVALID_SHORT_LINK("invalid_short_link", TimeUtils.getMills(1, TimeUnit.DAYS)),

    /**
     * 短链接统计判断是否新用户缓存标识
     */
    LINK_STATS_UV_KEY("link:stats:uv", -1L),

    /**
     * 短链接统计判断是否新 IP 缓存标识
     */
    LINK_STATS_UIP_KEY("link:stats:uip", -1L),
    /**
     * 消息队列消费完成幂等性
     */
    IDEMPOTENCY_ACCOMPLISH_KEY("rabbit:idempotency:accomplish", 1L, TimeUnit.DAYS),
    /**
    * 消息队列消费中状态幂等性
    */
    IDEMPOTENCY_PROCESSING_KEY("rabbit:idempotency:processing", 1L, TimeUnit.HOURS),
    ;

    private final String key;
    private final Long expire;
    private final TimeUnit timeUnit;

    RedisKeyEnum(String key, Long expire)
    {
        this.key = key.endsWith(":") ? key : key + ":";
        this.expire = expire;
        this.timeUnit = TimeUnit.SECONDS;
    }
    RedisKeyEnum(String key, Long expire, TimeUnit timeUnit) {
        this.key = key.endsWith(":") ? key : key + ":";
        this.expire = expire;
        this.timeUnit = timeUnit;
    }
}
