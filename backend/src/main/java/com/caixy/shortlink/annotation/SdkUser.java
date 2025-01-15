package com.caixy.shortlink.annotation;

import java.lang.annotation.*;

/**
 * 用于需要Sdk注入用户
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/15 2:35
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SdkUser {
}
