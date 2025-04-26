package com.caixy.shortlink.manager.email.models.enums;

/**
 * 基础邮件发送业务枚举类
 *
 * @Author CAIXYPROMISE
 * @since 2025/4/25 0:51
 */
public interface BaseEmailSenderEnum
{
    Integer getCode();
    String getName();
    String getTemplateName();
}
