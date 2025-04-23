package com.caixy.shortlink.manager.email.strategy.captcha;

import com.caixy.shortlink.manager.email.annotation.EmailSender;
import com.caixy.shortlink.manager.email.core.EmailContentGeneratorStrategy;
import com.caixy.shortlink.manager.email.exception.IllegalEmailParamException;
import com.caixy.shortlink.manager.email.models.EmailSenderEnum;
import com.caixy.shortlink.manager.email.models.captcha.BaseEmailActiveUserDTO;
import com.caixy.shortlink.manager.email.utils.FreeMarkEmailUtil;
import com.caixy.shortlink.utils.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 发送激活账户邮件
 *
 * @Author CAIXYPROMISE
 * @since 2025/2/11 22:23
 */

@EmailSender(EmailSenderEnum.ACTIVE_USER) // 激活账户
@Component
public class EmailActiveUserContentGenerator implements EmailContentGeneratorStrategy<BaseEmailActiveUserDTO>
{
    @Override
    public String getEmailContent(BaseEmailActiveUserDTO emailContentDTO, EmailSenderEnum emailSenderEnum)
    {
        if (StringUtils.isAnyBlank(emailContentDTO.getToken(), emailContentDTO.getCaptcha())) {
            throw new IllegalEmailParamException("验证码信息为空");
        }
        return FreeMarkEmailUtil.generateContent(emailSenderEnum.getTemplateName(), emailContentDTO);
    }
}
