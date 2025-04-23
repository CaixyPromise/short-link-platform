package com.caixy.shortlink.manager.rabbit.core.producer;


import com.caixy.shortlink.manager.rabbit.RabbitMQManager;
import com.caixy.shortlink.manager.rabbit.core.annotation.RabbitProducer;
import com.caixy.shortlink.manager.rabbit.core.enums.RabbitMQQueueEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用生产者方法类
 *
 * @author CAIXYPROMISE
 * @name com.caixy.adminSystem.mq.producer.core.GenericRabbitMQProducer
 * @since 2024-06-20 15:15
 **/
@Slf4j
public abstract class GenericRabbitMQProducer<T> implements RabbitMQProducerHandler<T>
{
    @Resource
    private RabbitMQManager rabbitMQUtils;

    protected final RabbitMQQueueEnum queueEnum;

    protected GenericRabbitMQProducer() {
        if (!getClass().isAnnotationPresent(RabbitProducer.class)) {
            throw new RuntimeException("Rabbit生产者初始化失败：RabbitProducer注解未找到");
        }
        queueEnum = getClass().getAnnotation(RabbitProducer.class).value();
    }

    /** 普通发送（非延迟） */
    @Override
    public void sendMessage(T message) {
        if (queueEnum.isDelay()) {
            // 延迟队列 → 走 sendDelayMessage
            log.debug("队列 [{}] 为延迟模式，自动切换到 sendDelayMessage()", queueEnum.getQueueName());
            sendDelayMessage(message);
            return;
        }
        rabbitMQUtils.sendMessage(queueEnum, message);
        log.info("发送普通消息成功，队列：{}，消息：{}", queueEnum.getQueueName(), message);
    }

    /** 延迟发送（PLUGIN 或 TTL） */
    @Override
    public void sendDelayMessage(T message) {
        if (!queueEnum.isDelay()) {
            log.warn("队列 [{}] 不是延迟队列，已改用 sendMessage()", queueEnum.getQueueName());
            rabbitMQUtils.sendMessage(queueEnum, message);
            return;
        }
        rabbitMQUtils.sendDelayedMessage(queueEnum, message);
        log.info("发送延迟消息成功，队列：{}，消息：{}", queueEnum.getQueueName(), message);
    }
}
