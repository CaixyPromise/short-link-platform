package com.caixy.shortlink.manager.rabbit.core.annotation;

import com.caixy.shortlink.manager.rabbit.core.enums.RabbitMQQueueEnum;

import java.lang.annotation.*;

/**
 * 自定义消息监听器
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 1:35
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RabbitConsumer
{
    RabbitMQQueueEnum value();
}
