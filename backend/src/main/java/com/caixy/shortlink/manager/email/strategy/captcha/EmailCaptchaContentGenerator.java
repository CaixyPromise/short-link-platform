package com.caixy.shortlink.manager.email.strategy.captcha;

import com.caixy.shortlink.manager.email.annotation.EmailSender;
import com.caixy.shortlink.manager.email.models.EmailSenderEnum;
import com.caixy.shortlink.manager.email.core.EmailContentGeneratorStrategy;
import com.caixy.shortlink.manager.email.exception.IllegalEmailParamException;
import com.caixy.shortlink.manager.email.models.captcha.BaseEmailCaptchaDTO;
import com.caixy.shortlink.manager.email.utils.FreeMarkEmailUtil;
import org.springframework.stereotype.Component;

/**
 * 验证码邮件处理器
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.Email.strategy.captcha.EmailCaptchaSender
 * @since 2024/10/7 上午12:54
 */
@EmailSender({                           // 分别处理以下业务的发送：
        EmailSenderEnum.RESET_PASSWORD,  // 修改密码
        EmailSenderEnum.REGISTER,        // 注册账号
        EmailSenderEnum.RESET_EMAIL,     // 修改邮箱
})
@Component
public class EmailCaptchaContentGenerator implements EmailContentGeneratorStrategy<BaseEmailCaptchaDTO>
{

    @Override
    public String getEmailContent(BaseEmailCaptchaDTO emailCaptchaDTO, EmailSenderEnum emailSenderEnum)
    {
        String code = emailCaptchaDTO.getCaptcha();
        if (code == null)
        {
            throw new IllegalEmailParamException("验证码信息为空");
        }
        return FreeMarkEmailUtil.generateContent(emailSenderEnum.getTemplateName(), emailCaptchaDTO);
    }
}
