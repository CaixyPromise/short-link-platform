package com.caixy.shortlink.manager.rabbit;

import com.caixy.shortlink.manager.rabbit.core.enums.RabbitMQQueueEnum;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.model.enums.RedisKeyEnum;
import com.caixy.shortlink.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * RabbitMq工具类
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 0:28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQManager
{

    private final RabbitTemplate rabbitTemplate;
    private final RedisManager redisManager;


    /**
     * 发送延迟消息
     *
     * @param queueEnum 队列枚举信息
     * @param message   要发送的消息内容
     */
    public void sendDelayedMessage(RabbitMQQueueEnum queueEnum, Object message)
    {
        if (queueEnum.getDelayTime() == null)
        {
            log.warn("消息发送失败: 延迟时间不能为空");
        }

        // 构建消息属性映射
        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("x-delay", queueEnum.getDelayTime());

        // 构建消息
        MessagePostProcessor messagePostProcessor = buildMessagePostProcessor(messageProperties);
        CorrelationData correlationData = buildCorrelationData();

        // 发送消息
        rabbitTemplate.convertAndSend(
                queueEnum.getExchange(),
                queueEnum.getRoutingKey(),
                message,
                messagePostProcessor,
                correlationData
        );
        log.info("发送延迟消息，消息ID为：{}", correlationData.getId());
    }

    /**
     * 发送延迟消息，并保持重试计数
     *
     * @param message    消息内容
     * @param retryCount 当前重试次数
     */
    public void sendDelayedMessageWithRetry(RabbitMQQueueEnum queueEnum, Object message, int retryCount)
    {
        // 构建消息属性映射
        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("x-delay", queueEnum.getDelayTime());
        messageProperties.put("x-retry-count", retryCount);

        // 构建消息处理器，将这些属性应用到消息上
        MessagePostProcessor messagePostProcessor = buildMessagePostProcessor(messageProperties);

        // 生成消息唯一标识
        CorrelationData correlationData = buildCorrelationData();

        // 发送消息
        rabbitTemplate.convertAndSend(
                queueEnum.getExchange(),
                queueEnum.getRoutingKey(),
                message,
                messagePostProcessor,
                correlationData
        );
        log.info("发送延迟消息，消息ID为：{}，重试次数为：{}", correlationData.getId(), retryCount);
    }



    /**
     * 发送非延迟消息
     *
     * @param queueEnum 队列枚举信息
     * @param message   要发送的消息内容
     */
    public void sendMessage(RabbitMQQueueEnum queueEnum, Object message)
    {
        CorrelationData correlationData = buildCorrelationData();

        rabbitTemplate.convertAndSend(
                queueEnum.getExchange(),
                queueEnum.getRoutingKey(),
                message,
                m -> {
                    m.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
                    return m;
                },
                correlationData
        );
        log.info("发送消息，消息ID为：{}", correlationData.getId());
    }

    private CorrelationData buildCorrelationData()
    {
        String messageId = UUID.randomUUID().toString();
        return new CorrelationData(messageId);
    }

    /**
     * 构建延迟消息的 MessagePostProcessor
     *
     * @param properties 要设置的消息属性映射
     * @return MessagePostProcessor
     */
    private MessagePostProcessor buildMessagePostProcessor(Map<String, Object> properties)
    {
        return msg ->
        {
            properties.forEach((key, value) -> msg.getMessageProperties().setHeader(key, value));
            msg.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
            return msg;
        };
    }
}
