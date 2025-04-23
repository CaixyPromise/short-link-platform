package com.caixy.shortlink.service;

import com.caixy.shortlink.manager.email.models.BaseEmailContentDTO;
import com.caixy.shortlink.manager.email.models.EmailSenderEnum;
import com.caixy.shortlink.manager.email.models.captcha.BaseEmailCaptchaDTO;
import com.caixy.shortlink.model.dto.email.SendEmailRequest;
import com.caixy.shortlink.model.vo.user.UserVO;

/**
 * @Name: com.caixy.shortlink.service.EmailService
 * @Description: 邮箱服务类
 * @Author: CAIXYPROMISE
 * @Date: 2024-01-10 22:00
 **/
public interface EmailService
{

    void sendCaptchaEmail(String toEmail, BaseEmailCaptchaDTO emailContentDTO, EmailSenderEnum senderEnum);

    void sendEmail(BaseEmailContentDTO emailContentDTO, String toEmail, EmailSenderEnum senderEnum);

    Boolean sendEmail(SendEmailRequest sendEmailRequest, EmailSenderEnum senderEnum, UserVO userInfo);

    void verifyCaptcha(EmailSenderEnum emailSenderEnum, String toEmail, String code);
}
