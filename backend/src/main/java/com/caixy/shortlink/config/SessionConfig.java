package com.caixy.shortlink.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Session配置
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.config.SessionConfig
 * @since 2024/10/28 18:50
 */
@Configuration
@EnableRedisHttpSession
public class SessionConfig
{
}