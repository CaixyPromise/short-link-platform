package com.caixy.shortlink.config;

import com.caixy.shortlink.config.properties.OAuth2ClientProperties;
import com.caixy.shortlink.manager.OAuth.annotation.InjectOAuthConfig;
import lombok.Data;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * OAuth2配置类
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.config.OAuth2ClientConfig
 * @since 2024/8/2 下午3:10
 */
@Data
@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig implements BeanPostProcessor
{
    private static final Logger log = LoggerFactory.getLogger(OAuth2ClientConfig.class);

    private final OAuth2ClientProperties oAuth2Properties;

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
            if (field.isAnnotationPresent(InjectOAuthConfig.class))
            {
                InjectOAuthConfig annotation = field.getAnnotation(InjectOAuthConfig.class);
                field.setAccessible(true);
                try
                {
                    OAuth2ClientProperties.OAuth2Client oAuth2Client = oAuth2Properties.getInstance(annotation.clientName());
                    Optional<OAuth2ClientProperties.OAuth2Client> optionalOAuth2Client = Optional.ofNullable(oAuth2Client);
                    if (optionalOAuth2Client.isPresent())
                    {
                        field.set(bean, oAuth2Client);
                        log.info("注入OAuth2配置成功: {}", field.getName());
                    }
                    else {
                        throw new RuntimeException("OAuth2配置注入失败，未找到对应的配置信息");
                    }
                }
                catch (IllegalAccessException e)
                {
                    log.error("注入OAuth2配置失败: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}
