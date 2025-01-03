package com.caixy.shortlink.manager.Email.strategy;

import com.caixy.shortlink.manager.Email.annotation.EmailSender;
import com.caixy.shortlink.manager.Email.core.EmailSenderEnum;
import com.caixy.shortlink.manager.Email.core.EmailSenderStrategy;
import com.caixy.shortlink.manager.Email.exception.IllegalEmailParamException;
import com.caixy.shortlink.manager.Email.models.captcha.EmailCaptchaConstant;
import com.caixy.shortlink.manager.Email.models.captcha.EmailCaptchaDTO;
import com.caixy.shortlink.manager.Email.utils.FreeMarkEmailUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 验证码邮件处理器
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.Email.strategy.EmailCaptchaSender
 * @since 2024/10/7 上午12:54
 */
@EmailSender({                           // 分别处理以下业务的发送：
        EmailSenderEnum.RESET_PASSWORD,  // 修改密码
        EmailSenderEnum.REGISTER,        // 注册账号
        EmailSenderEnum.ACTIVE_USER,     // 激活用户
        EmailSenderEnum.RESET_EMAIL,     // 修改邮箱
})
@Component
public class EmailCaptchaSender implements EmailSenderStrategy
{

    @Override
    public String getEmailContent(Map<String, Object> params)
    {
        Object code = params.get(EmailCaptchaConstant.CACHE_KEY_CODE);
        if (code == null)
        {
            throw new IllegalEmailParamException("验证码信息为空");
        }
        // 创建邮箱内容实体类
        EmailCaptchaDTO emailCaptchaDTO = new EmailCaptchaDTO();
        emailCaptchaDTO.setCaptcha(code.toString());
        return FreeMarkEmailUtil.generateContent(EmailSenderEnum.RESET_PASSWORD.getTemplateName(), emailCaptchaDTO);
    }
}
