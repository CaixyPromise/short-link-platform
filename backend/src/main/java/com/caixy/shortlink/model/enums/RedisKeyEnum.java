package com.caixy.shortlink.model.enums;

import com.caixy.shortlink.common.BaseCacheableEnum;
import com.caixy.shortlink.utils.TimeUtils;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum RedisKeyEnum implements BaseCacheableEnum
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
    * 跳转链接缓存
    */
    TARGET_SHORT_LINK("target_short_link:", -1L),

    /**
    * 无效短链接缓存，持续一天
    */
    INVALID_SHORT_LINK("invalid_short_link", TimeUtils.getMills(1, TimeUnit.DAYS))
    ;

    private final String key;
    private final Long expire;

    RedisKeyEnum(String key, Long expire)
    {
        this.key = key.endsWith(":") ? key : key + ":";
        this.expire = expire;
    }
}
