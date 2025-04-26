package com.caixy.shortlink.service;

import com.caixy.shortlink.manager.email.models.common.BaseEmailContentDTO;
import com.caixy.shortlink.manager.email.models.enums.BaseEmailSenderEnum;
import com.caixy.shortlink.manager.email.models.enums.EmailCaptchaBizEnum;

/**
 * @Name: com.caixy.shortlink.service.EmailService
 * @Description: 邮箱服务类
 * @Author: CAIXYPROMISE
 * @Date: 2024-01-10 22:00
 **/
public interface EmailService
{

    void sendEmail(String toEmail, BaseEmailContentDTO emailContentDTO, BaseEmailSenderEnum senderEnum);

    void verifyCaptcha(EmailCaptchaBizEnum emailSenderEnum, String toEmail, String code);
}
