package com.caixy.shortlink.manager.email;

import com.caixy.shortlink.config.EmailConfig;

import com.caixy.shortlink.manager.email.constant.EmailConstant;
import com.caixy.shortlink.manager.email.models.BaseEmailContentDTO;
import com.caixy.shortlink.manager.email.models.EmailSenderEnum;
import com.caixy.shortlink.manager.email.core.EmailContentGeneratorStrategy;
import com.caixy.shortlink.manager.email.exception.IllegalEmailParamException;
import com.caixy.shortlink.manager.email.factory.EmailSenderFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.caixy.shortlink.utils.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.concurrent.*;

/**
 * 邮件发送管理器
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.Email.EmailSenderManager
 * @since 2024/10/1 下午3:45
 */
@Slf4j
@Component
@AllArgsConstructor
public class EmailSenderManager
{
    private final JavaMailSender mailSender;

    private final EmailConfig emailConfig;
    private static final String baseSubjectTitle = String.format("【%s】-", EmailConstant.PLATFORM_NAME_CN);


    private final EmailSenderFactory emailSenderFactory;

    /**
     * 异步发送邮件
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/9 上午12:07
     */
    public void doSendBySync(EmailSenderEnum senderEnum, String toEmail, BaseEmailContentDTO emailContentDTO)
    {
        CompletableFuture.runAsync(() -> {
            boolean sendEmail = sendEmail(senderEnum, toEmail, emailContentDTO);
            printResultLog(toEmail, senderEnum.getName(), sendEmail);
        }, Executors.newVirtualThreadPerTaskExecutor());
        // ⭐ Tips: Here's using virtual thread pool by Executors, required java 21 and Spring Boot 3.3.*↑
        // But poling virtual thread pool may not be the recommended. :)
        // Just because virtual thread is very lightweight.
    }

    /**
     * 阻塞发送邮件
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/9 上午12:07
     */
    public boolean doSend(EmailSenderEnum senderEnum, String toEmail, BaseEmailContentDTO emailContentDTO)
    {
        boolean sendEmail = sendEmail(senderEnum, toEmail, emailContentDTO);
        printResultLog(toEmail, senderEnum.getName(), sendEmail);
        return sendEmail;
    }

    private boolean sendEmail(EmailSenderEnum senderEnum, String toEmail, BaseEmailContentDTO emailContentDTO)
    {
        try
        {
            EmailContentGeneratorStrategy<BaseEmailContentDTO> strategy = emailSenderFactory.getStrategy(senderEnum);
            String emailContent = strategy.getEmailContent(emailContentDTO, senderEnum);
            if (StringUtils.isBlank(emailContent))
            {
                log.error("邮件内容为空");
                return false;
            }
            if (StringUtils.isBlank(toEmail))
            {
                log.error("邮件接收人为空");
                return false;
            }
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            // 邮箱发送内容组成
            // 构建Title
            helper.setSubject(buildSubjectTitle(senderEnum));
            // 构建内容
            helper.setText(emailContent, true);
            // 构建收信人
            helper.setTo(toEmail);
            // 构建发送人
            helper.setFrom(EmailConstant.BASE_EMAIL_TITLE + '<' + emailConfig.getUsername() + '>');
            mailSender.send(message);
            return true;
        }
        catch (MessagingException | IllegalEmailParamException e)
        {
            log.error("邮件： {}，类型：{}，发送失败：{}", toEmail, senderEnum.getName(), e.getMessage());
            return false;
        }
    }

    private String buildSubjectTitle(EmailSenderEnum emailSenderEnum)
    {
        return baseSubjectTitle + emailSenderEnum.getName();
    }

    private void printResultLog(String email, String scenes, boolean result)
    {
        if (result)
        {
            log.info("目标邮箱：{}, 场景：{}, 邮件发送成功", email, scenes);
        }
        else
        {
            log.error("目标邮箱：{}, 场景：{}, 邮件发送失败", email, scenes);
        }
    }
}
