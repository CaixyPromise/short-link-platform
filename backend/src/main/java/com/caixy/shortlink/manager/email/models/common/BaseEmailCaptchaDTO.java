package com.caixy.shortlink.manager.email.models.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Email内容：验证码DTO
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.Email.models.captcha.EmailCaptchaDTO
 * @since 2024/10/7 上午12:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEmailCaptchaDTO extends BaseEmailContentDTO
{
    /**
     * 验证码
     */
    private String captcha;

    private static final long serialVersionUID = 1L;
}