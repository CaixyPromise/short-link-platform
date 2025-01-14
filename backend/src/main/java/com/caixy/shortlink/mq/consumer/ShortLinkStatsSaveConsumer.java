package com.caixy.shortlink.mq.consumer;

import com.caixy.shortlink.manager.rabbit.core.annotation.RabbitConsumer;
import com.caixy.shortlink.manager.rabbit.core.consumer.GenericRabbitMQConsumer;
import com.caixy.shortlink.manager.rabbit.core.enums.RabbitMQQueueEnum;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkStatsRecordDTO;
import com.caixy.shortlink.service.LinkAccessStatsService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

/**
 * 链接访问记录消费者
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 17:16
 */
@Component
@RequiredArgsConstructor
@RabbitConsumer(RabbitMQQueueEnum.LINK_STATS)
public class ShortLinkStatsSaveConsumer extends GenericRabbitMQConsumer<ShortLinkStatsRecordDTO>
{
    private final LinkAccessStatsService linkAccessStatsService;

    @Override
    public void handleMessage(ShortLinkStatsRecordDTO messageDto, Channel channel, Message message,
                              String messageId)
    {
        if (messageDto == null)
        {
            discardMessage(channel, message);
            return;
        }
        linkAccessStatsService.saveShortLinkStats(messageDto);
    }

    @Override
    public void handleDeadLetterMessage(ShortLinkStatsRecordDTO messageDto, Channel channel, Message message,
                                        String messageId)
    {
        linkAccessStatsService.saveShortLinkStats(messageDto);
    }
}
