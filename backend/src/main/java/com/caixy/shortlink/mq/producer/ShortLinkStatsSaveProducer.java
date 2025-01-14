package com.caixy.shortlink.mq.producer;

import com.caixy.shortlink.manager.rabbit.core.enums.RabbitMQQueueEnum;
import com.caixy.shortlink.manager.rabbit.core.producer.GenericRabbitMQProducer;
import com.caixy.shortlink.manager.rabbit.core.annotation.RabbitProducer;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkStatsRecordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 短链监控信息消息队列生产者
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 0:53
 */
@RabbitProducer(RabbitMQQueueEnum.LINK_STATS)
@RequiredArgsConstructor
@Component
public class ShortLinkStatsSaveProducer extends GenericRabbitMQProducer<ShortLinkStatsRecordDTO>
{

}
