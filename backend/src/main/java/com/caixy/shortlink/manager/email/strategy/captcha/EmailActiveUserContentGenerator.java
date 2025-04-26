package com.caixy.shortlink.manager.email.strategy.captcha;

import com.caixy.shortlink.manager.email.annotation.EmailSender;
import com.caixy.shortlink.manager.email.core.EmailContentGeneratorStrategy;
import com.caixy.shortlink.manager.email.exception.IllegalEmailParamException;
import com.caixy.shortlink.manager.email.models.enums.BaseEmailSenderEnum;
import com.caixy.shortlink.manager.email.models.enums.EmailCaptchaBizEnum;
import com.caixy.shortlink.manager.email.models.captcha.EmailActiveUserDTO;
import com.caixy.shortlink.manager.email.utils.FreeMarkEmailUtil;
import com.caixy.shortlink.utils.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 发送激活账户邮件
 *
 * @Author CAIXYPROMISE
 * @since 2025/2/11 22:23
 */

@EmailSender(captcha = {EmailCaptchaBizEnum.ACTIVE_USER}) // 激活账户
@Component
public class EmailActiveUserContentGenerator implements EmailContentGeneratorStrategy<EmailActiveUserDTO>
{
    @Override
    public String getEmailContent(EmailActiveUserDTO emailContentDTO, BaseEmailSenderEnum emailSenderEnum)
    {
        if (StringUtils.isAnyBlank(emailContentDTO.getToken(), emailContentDTO.getCaptcha())) {
            throw new IllegalEmailParamException("验证码信息为空");
        }
        return FreeMarkEmailUtil.generateContent(emailSenderEnum.getTemplateName(), emailContentDTO);
    }
}
