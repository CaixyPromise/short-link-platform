package com.caixy.shortlink.config;


import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP流量配置
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.config.HttpTraceActuatorConfiguration
 * @since 2024/10/31 22:13
 */
@Configuration
public class HttpTraceActuatorConfiguration
{
    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }
}