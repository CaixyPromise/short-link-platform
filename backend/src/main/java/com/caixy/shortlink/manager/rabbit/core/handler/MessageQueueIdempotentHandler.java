package com.caixy.shortlink.manager.rabbit.core.handler;

import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.model.enums.RedisKeyEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 消息队列幂等处理器
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 18:52
 */
@Component
@RequiredArgsConstructor
public class MessageQueueIdempotentHandler
{
    private final RedisManager redisManager;

    /**
     * 判断当前消息是否正在被消费
     *
     * @param messageId 消息唯一标识
     * @return 消息是否正在被消费
     */
    public boolean isMessageBeingConsumed(String messageId)
    {
        return redisManager.hasKey(RedisKeyEnum.IDEMPOTENCY_PROCESSING_KEY, messageId);
    }

    /**
     * 判断消息消费流程是否执行完成
     *
     * @param messageId 消息唯一标识
     * @return 消息是否执行完成
     */
    public boolean isAccomplish(String messageId)
    {
        return redisManager.hasKey(RedisKeyEnum.IDEMPOTENCY_ACCOMPLISH_KEY, messageId);
    }

    /**
     * 设置消息为正在被消费
     *
     * @param messageId 消息唯一标识
     * @return 是否成功标记为正在消费（避免重复处理）
     */
    public boolean setMessageInProcess(String messageId)
    {
        return redisManager.setIfAbsent(RedisKeyEnum.IDEMPOTENCY_PROCESSING_KEY, "1", messageId);
    }

    /**
     * 设置消息消费流程执行完成
     *
     * @param messageId 消息唯一标识
     */
    public void setAccomplish(String messageId)
    {
        redisManager.setString(RedisKeyEnum.IDEMPOTENCY_ACCOMPLISH_KEY, "1", messageId);
    }

    /**
     * 如果消息处理遇到异常情况，删除幂等标识
     *
     * @param messageId 消息唯一标识
     */
    public void delMessageProcessed(String messageId)
    {
        redisManager.delete(RedisKeyEnum.IDEMPOTENCY_PROCESSING_KEY, messageId);

        redisManager.delete(RedisKeyEnum.IDEMPOTENCY_ACCOMPLISH_KEY, messageId);
    }
}
