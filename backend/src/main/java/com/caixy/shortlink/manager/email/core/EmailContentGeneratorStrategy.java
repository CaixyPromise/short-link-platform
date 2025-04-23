package com.caixy.shortlink.manager.email.core;

import com.caixy.shortlink.manager.email.models.BaseEmailContentDTO;
import com.caixy.shortlink.manager.email.models.EmailSenderEnum;

/**
 * 邮件发送策略接口
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.Email.core.EmailSenderStrategy
 * @since 2024/10/7 上午12:53
 */
public interface EmailContentGeneratorStrategy<T extends BaseEmailContentDTO>
{
    String getEmailContent(T emailContentDTO, EmailSenderEnum emailSenderEnum);
}
