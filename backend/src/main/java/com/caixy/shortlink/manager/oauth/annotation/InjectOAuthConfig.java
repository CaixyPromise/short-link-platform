package com.caixy.shortlink.manager.oauth.annotation;

import com.caixy.shortlink.model.enums.OAuthProviderEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入OAuth服务端配置
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.OAuth.annotation.InjectOAuthConfig
 * @since 2024/8/3 上午2:04
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectOAuthConfig
{
    OAuthProviderEnum clientName();
    String name() default "";
}
