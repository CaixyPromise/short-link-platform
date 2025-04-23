package com.caixy.shortlink.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 0:14
 */
@Slf4j
@Configuration
public class RabbitMqConfiguration
{
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    //配置连接工厂
    @Bean
    public CachingConnectionFactory cachingConnectionFactory()
    {
        return new CachingConnectionFactory(rabbitmqHost);
    }

    @Bean
    public RabbitAdmin rabbitAdmin()
    {
        //需要传入
        RabbitAdmin rabbitAdmin = new RabbitAdmin(cachingConnectionFactory());
//        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory myFactory(ConnectionFactory connectionFactory)
    {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置为手动确认
        // 设置消息转换器为 Jackson2JsonMessageConverter，支持 application/json
        factory.setMessageConverter(new Jackson2JsonMessageConverter());

        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory)
    {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // 设置消息转换器为 Jackson2JsonMessageConverter，支持 application/json
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        // 设置确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData != null) {
                String id = correlationData.getId();
                if (ack) {
                    log.info("Message sent successfully: {}", id);
                } else {
                    log.error("Message failed to send: {}, cause: {}", id, cause);
                }
            }
        });

        // 设置返回回调，用于处理消息没有路由到队列的情况
        rabbitTemplate.setReturnsCallback(returned -> {
            log.info("Message returned: {}", returned.getMessage());
            log.info("Exchange: {}", returned.getExchange());
            log.info("RoutingKey: {}", returned.getRoutingKey());
            log.info("ReplyText: {}", returned.getReplyText());
        });

        return rabbitTemplate;
    }
}
