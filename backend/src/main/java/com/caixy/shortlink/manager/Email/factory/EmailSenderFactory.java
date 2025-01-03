package com.caixy.shortlink.manager.Email.factory;

import com.caixy.shortlink.manager.Email.annotation.EmailSender;
import com.caixy.shortlink.manager.Email.core.EmailSenderEnum;
import com.caixy.shortlink.manager.Email.core.EmailSenderStrategy;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

/**
 * Email发送类工厂
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.Email.factory.EmailSenderFactory
 * @since 2024/10/8 上午1:07
 */
@Service
@AllArgsConstructor
public class EmailSenderFactory
{
    private static final Map<EmailSenderEnum, EmailSenderStrategy> strategies = new HashMap<>();

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init()
    {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(EmailSender.class);

        for (Object bean : beans.values())
        {
            // 获取类上的 @EmailSenderStrategy 注解
            EmailSender annotation = bean.getClass().getAnnotation(EmailSender.class);
            // 获取注解中的所有枚举值
            EmailSenderEnum[] emailSenderEnum = annotation.value();
            for (EmailSenderEnum type : emailSenderEnum)
            {
                // 注册到策略 Map 中
                strategies.put(type, (EmailSenderStrategy) bean);
            }
        }
    }

    // 获取对应的邮件发送策略
    public EmailSenderStrategy getStrategy(EmailSenderEnum emailSenderEnum)
    {
        EmailSenderStrategy strategy = strategies.get(emailSenderEnum);
        if (strategy == null)
        {
            throw new IllegalArgumentException("No strategy found for email type: " + emailSenderEnum);
        }
        return strategy;
    }
}
