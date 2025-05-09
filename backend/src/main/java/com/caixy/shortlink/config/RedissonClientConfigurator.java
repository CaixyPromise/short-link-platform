package com.caixy.shortlink.config;

import com.caixy.shortlink.annotation.InjectRedissonClient;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.config.properties.RedissonProperties;
import com.caixy.shortlink.exception.ThrowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;

/**
 * RedisSession会话管理，用来配置分布式redis配置，根据要求客户端类型加载对应的客户端
 *
 * @name: com.caixy.project.manager.RedissonClientConfigurator
 * @author: CAIXYPROMISE
 * @since: 2024-01-07 22:01
 **/
@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedissonClientConfigurator implements BeanPostProcessor
{
    private final RedissonProperties redissonProperties;

    @Override
    public Object postProcessBeforeInitialization( Object bean,  String beanName) throws BeansException
    {
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean,  String beanName)
    {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(InjectRedissonClient.class))
            {
                InjectRedissonClient annotation = field.getAnnotation(InjectRedissonClient.class);
                field.setAccessible(true);
                try
                {
                    field.set(bean, createRedissonClient(annotation.clientName()));
                    log.info("注入RedissonClient成功，clientName: {}", annotation.name());
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(annotation.name() + "注入RedissonClient失败");
                }
            }
        }
        return bean;
    }

    private RedissonClient createRedissonClient(String clientName)
    {
        RedissonProperties.RedisInstanceProperties properties = redissonProperties.getInstances().get(clientName);
        ThrowUtils.throwIf(properties == null, ErrorCode.SYSTEM_ERROR, "Redisson配置不存在");
        Config config = new Config();
        String host = properties.getHost();
        if (!host.contains("redis://"))
        {
            host = "redis://" + host;
        }
        config.useSingleServer()
                .setAddress(host + ":" + properties.getPort())
                .setDatabase(properties.getDatabase())
                .setPassword(properties.getPassword() == null ? null : properties.getPassword());
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient createRedissonClient() {
        return createRedissonClient("default");
    }
}
