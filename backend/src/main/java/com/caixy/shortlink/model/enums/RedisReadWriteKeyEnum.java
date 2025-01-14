package com.caixy.shortlink.model.enums;

import com.caixy.shortlink.common.BaseCacheEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * Redis读写锁枚举
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 17:02
 */
@Getter
public enum RedisReadWriteKeyEnum implements BaseCacheEnum
{
    LINK_STAT("link_stat")
    ;
    private final String key;
    private final Long expire;
    private final TimeUnit timeUnit;
    RedisReadWriteKeyEnum(String key) {
        this.key = key;
        this.expire = null;
        this.timeUnit = null;
    }
}
