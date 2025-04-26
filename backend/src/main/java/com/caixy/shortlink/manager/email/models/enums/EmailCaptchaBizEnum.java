package com.caixy.shortlink.manager.email.models.enums;

import cn.hutool.core.util.RandomUtil;
import com.caixy.shortlink.common.BaseCacheEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * 验证码发送业务枚举
 *
 * @Author CAIXYPROMISE
 * @since 2025/4/25 0:56
 */
@Getter
@AllArgsConstructor
public enum EmailCaptchaBizEnum implements BaseCacheEnum, BaseEmailSenderEnum
{
    RESET_EMAIL(
            0,
            "modify_email",
            "common-captcha.html.ftl",
            "reset_email",
            5L,
            TimeUnit.MINUTES,
            EmailCaptchaTypeEnum.NUMBER,
            6
            ),
    ACTIVE_USER(
            5,
            "activate_user",
            "active-account.html.ftl",
            "active_user",
            5L,
            TimeUnit.MINUTES,
            EmailCaptchaTypeEnum.NUMBER,
            6),
    RESET_PASSWORD(10,
            "reset_password",
            "common-captcha.html.ftl",
            "reset_psw",
            5L,
            TimeUnit.MINUTES,
            EmailCaptchaTypeEnum.NUMBER,
            6),
    ;
    private final Integer code;
    private final String name;
    private final String templateName;
    private final String key;
    private final Long expire;
    private final TimeUnit timeUnit;
    private final EmailCaptchaTypeEnum captchaTypeEnum;
    private final Integer captchaLength;

    public String generateCaptchaCode() {
        return switch (captchaTypeEnum)
        {
            case NUMBER -> RandomUtil.randomNumbers(captchaLength);
            case ALPHA -> RandomUtil.randomStringUpper(captchaLength);
            case MIXTURE -> RandomUtil.randomString(captchaLength);
        };
    }
}
