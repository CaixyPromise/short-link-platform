package com.caixy.shortlink.manager.rabbit.core.consumer;

import com.caixy.shortlink.manager.rabbit.core.annotation.RabbitConsumer;
import com.caixy.shortlink.manager.rabbit.core.enums.DelayMode;
import com.caixy.shortlink.manager.rabbit.core.enums.RabbitMQQueueEnum;
import com.caixy.shortlink.manager.rabbit.core.handler.RabbitConsumerPostHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 消费者队列注册器
 *
 * @Author CAIXYPROMISE
 * @since 2025/4/22 4:25
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitConsumerRegistrar
        implements org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer, BeanFactoryAware, ApplicationContextAware
{

    private ListableBeanFactory beanFactory;
    private ApplicationContext  ctx;
    private final RabbitConsumerPostHandler postHandler;

    @Override public void setBeanFactory(org.springframework.beans.factory.BeanFactory bf) throws BeansException
    {
        this.beanFactory = (ListableBeanFactory) bf;
    }
    @Override public void setApplicationContext(ApplicationContext ac) { this.ctx = ac; }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {

        RabbitAdmin admin = ctx.getBean(RabbitAdmin.class);
        Map<String,Object> beans = beanFactory.getBeansWithAnnotation(RabbitConsumer.class);

        beans.forEach((beanName, bean) -> {

            Class<?> clazz = AopUtils.getTargetClass(bean);
            RabbitConsumer meta = clazz.getAnnotation(RabbitConsumer.class);
            RabbitMQQueueEnum v = meta.value();

            /* ---------- 1. 声明 Exchange ---------- */
            Exchange exchange = switch (v.getDelayMode()) {
                case PLUGIN -> new CustomExchange(v.getExchange(), "x-delayed-message", true, false,
                        Map.of("x-delayed-type", v.getDelayedType()));
                default     -> ExchangeBuilder.directExchange(v.getExchange()).durable(true).build();
            };
            admin.declareExchange(exchange);

            /* ---------- 2. 声明 Queue ---------- */
            Queue queue = QueueBuilder.durable(v.getQueueName())
                    .withArguments(v.queueArgs())
                    .build();
            admin.declareQueue(queue);

            /* 3. 声明 Binding —— 兼容 CustomExchange */
            Binding binding = (exchange instanceof DirectExchange de) ?
                    BindingBuilder.bind(queue).to(de).with(v.getRoutingKey()) :
                    new Binding(queue.getName(), Binding.DestinationType.QUEUE,
                            exchange.getName(), v.getRoutingKey(), null);
            admin.declareBinding(binding);

            /* ---------- 4. 如需 Dead‑Letter 队列，再补 DLQ 交换机/队列 ---------- */
            if (v.getDelayMode() == DelayMode.TTL && v.getDeadLetterQueue() != null) {
                String dlxName = v.getDeadLetterQueue() + ".dlx";
                DirectExchange dlx = new DirectExchange(dlxName, true, false);
                Queue dlq = QueueBuilder.durable(v.getDeadLetterQueue()).build();
                admin.declareExchange(dlx);
                admin.declareQueue(dlq);
                admin.declareBinding(BindingBuilder.bind(dlq).to(dlx).with(v.getDeadLetterQueue() + ".dlq"));
            }

            log.info("✅ Declared X [{}], Q [{}], RK [{}]", exchange.getName(), queue.getName(), v.getRoutingKey());

            /* ---------- 5. 注册监听端点 ---------- */
            ChannelAwareMessageListener listener = postHandler.getListener(beanName);
            if (listener == null)
                throw new IllegalStateException("No wrapped listener found for " + beanName);

            SimpleRabbitListenerEndpoint ep = new SimpleRabbitListenerEndpoint();
            ep.setId(beanName + "#" + v.getQueueName());
            ep.setQueueNames(v.getQueueName());
            ep.setConcurrency(meta.concurrency());
            ep.setAutoStartup(meta.autoStartup());
            ep.setMessageListener(listener);
            registrar.registerEndpoint(ep);
        });
    }
}
