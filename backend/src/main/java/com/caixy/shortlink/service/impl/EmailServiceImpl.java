package com.caixy.shortlink.service.impl;


import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.manager.email.EmailSenderManager;
import com.caixy.shortlink.manager.email.models.common.BaseEmailContentDTO;
import com.caixy.shortlink.manager.email.models.enums.BaseEmailSenderEnum;
import com.caixy.shortlink.manager.email.models.enums.EmailCaptchaBizEnum;
import com.caixy.shortlink.manager.email.models.captcha.EmailCaptchaConstant;
import com.caixy.shortlink.manager.email.models.common.BaseEmailCaptchaDTO;
import com.caixy.shortlink.service.EmailService;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.utils.RegexUtils;
import com.caixy.shortlink.utils.ServletUtils;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.caixy.shortlink.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱服务类实现
 *
 * @name: com.caixy.shortlink.service.impl.EmailServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2024-01-10 22:01
 **/
@Service
@Slf4j
@AllArgsConstructor
public class EmailServiceImpl implements EmailService
{
    private final EmailSenderManager emailSenderManager;
    private final RedisManager redisManager;
    private final static String BASE_KEY_FORMATTED = "%s_%s_%s:"; // 格式化key：key类型-邮件模板名称-邮箱
    private final static String SENT_KEY = "EMAIL_SENT";
    private final static String CAPTCHA_CODE_KEY = "EMAIL_CAPTCHA_CODE";

    /**
    * 发送普通邮件
    */
    @Override
    public void sendEmail(String toEmail, BaseEmailContentDTO emailContentDTO, BaseEmailSenderEnum senderEnum)
    {
        if (StringUtils.isBlank(toEmail) || !RegexUtils.isEmail(toEmail)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        //检查是否发送
        checkSent(toEmail, senderEnum);
        if (senderEnum instanceof EmailCaptchaBizEnum captchaBizEnum) {
            if (emailContentDTO instanceof BaseEmailCaptchaDTO captchaDTO) {
                doSendCaptcha(toEmail, captchaBizEnum, captchaDTO);
            } else {
                log.error("emailContentDTO is not instanceof BaseEmailCaptchaDTO");
                return; //  不符合规范，直接不发送
            }
        }
        emailSenderManager.doSendBySync(senderEnum, toEmail, emailContentDTO);
    }

    /**
     * 校验验证码
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/30 2:32
     */
    @Override
    public void verifyCaptcha(EmailCaptchaBizEnum emailSenderEnum, String toEmail, String code)
    {
        if (StringUtils.isAnyBlank(toEmail, code) || !RegexUtils.isEmail(toEmail))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱或验证码为空");
        }
        // 从Redis中获取验证码
        String cachedCode = redisManager.getString(getRedisKey(CAPTCHA_CODE_KEY, toEmail, emailSenderEnum));
        if (cachedCode == null || cachedCode.isEmpty())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码已过期，请重新获取");
        }
        // 验证码校验
        if (!cachedCode.equals(code))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        // 验证码正确，删除缓存中的验证码
        redisManager.delete(getRedisKey(CAPTCHA_CODE_KEY, toEmail, emailSenderEnum));

    }

    /**
     * 发送验证码统一方法
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/10 下午7:11
     */
    private void doSendCaptcha(String toEmail, EmailCaptchaBizEnum senderEnum, @NotNull BaseEmailCaptchaDTO captchaContentDTO)
    {
        // 检查目标邮箱是否为空
        ThrowUtils.throwIf(StringUtils.isBlank(toEmail), ErrorCode.PARAMS_ERROR);
        // 生成验证码
        String code = senderEnum.generateCaptchaCode();
        // 设置redis缓存信息，验证码，邮箱信息
        captchaContentDTO.setCaptcha(code);
        // 将验证码存入Redis，设置过期时间为5分钟
        redisManager.setString(getRedisKey(CAPTCHA_CODE_KEY, toEmail, senderEnum), code, senderEnum.getExpire(), senderEnum.getTimeUnit());
    }


    /**
     * 检查是否发送
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/10 下午7:12
     */
    private void checkSent(String toEmail, BaseEmailSenderEnum senderEnum)
    {
        // 检查目标邮箱
        ThrowUtils.throwIf(StringUtils.isBlank(toEmail), ErrorCode.PARAMS_ERROR, "邮箱不得为空");
        // 检查1分钟之内是否重复发送
        boolean hasSend = redisManager.setIfAbsent(getRedisKey(SENT_KEY, toEmail, senderEnum),  "1", 60L, TimeUnit.SECONDS);
        if (!hasSend)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮件已发送，请到邮箱内查收。");
        }
    }

    private String getRedisKey(String formatted, String toEmail, BaseEmailSenderEnum senderEnum) {
        return BASE_KEY_FORMATTED.formatted(formatted, senderEnum.getName(), toEmail);
    }
}
