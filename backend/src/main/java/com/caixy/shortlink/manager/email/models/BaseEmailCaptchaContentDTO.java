package com.caixy.shortlink.manager.email.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础邮件验证码内容模板
 *
 * @Author CAIXYPROMISE
 * @since 2025/2/11 21:50
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEmailCaptchaContentDTO extends BaseEmailContentDTO
{
    /**
     * 验证码
     */
    private String captcha;
}
