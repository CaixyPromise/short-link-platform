package com.caixy.shortlink.manager.email.models;

import com.caixy.shortlink.manager.email.models.enums.BaseEmailSenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件纯文本业务枚举
 *
 * @Author CAIXYPROMISE
 * @since 2025/4/25 1:07
 */
@Getter
@AllArgsConstructor
public enum EmailTextBizEnum implements BaseEmailSenderEnum
{

    ;
    private final Integer code;
    private final String name;
    private final String templateName;
}
