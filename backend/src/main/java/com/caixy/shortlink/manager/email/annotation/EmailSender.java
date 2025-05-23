package com.caixy.shortlink.manager.email.annotation;

import com.caixy.shortlink.manager.email.models.enums.EmailCaptchaBizEnum;
import com.caixy.shortlink.manager.email.models.EmailTextBizEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Email发送者处理器
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.Email.annotation.EmailSender
 * @since 2024/10/6 下午6:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EmailSender
{
    EmailCaptchaBizEnum[] captcha() default {};
    EmailTextBizEnum[] text() default {};
}
