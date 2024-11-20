package com.caixy.shortlink.service;

import com.caixy.shortlink.manager.Email.core.EmailSenderEnum;
import com.caixy.shortlink.model.dto.email.SendEmailRequest;
import com.caixy.shortlink.model.vo.user.UserVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Name: com.caixy.shortlink.service.EmailService
 * @Description: 邮箱服务类
 * @Author: CAIXYPROMISE
 * @Date: 2024-01-10 22:00
 **/
public interface EmailService
{

    Boolean sendEmail(SendEmailRequest sendEmailRequest, EmailSenderEnum senderEnum, HttpServletRequest request,
                      UserVO userInfo);
}
