package com.caixy.shortlink.service.impl;


import cn.hutool.core.util.RandomUtil;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.manager.email.EmailSenderManager;
import com.caixy.shortlink.manager.email.models.BaseEmailContentDTO;
import com.caixy.shortlink.manager.email.models.EmailSenderEnum;
import com.caixy.shortlink.manager.email.models.captcha.EmailCaptchaConstant;
import com.caixy.shortlink.manager.email.models.captcha.BaseEmailCaptchaDTO;
import com.caixy.shortlink.model.dto.email.SendEmailRequest;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.EmailService;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.utils.ServletUtils;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.caixy.shortlink.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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


    /**
    * 发送验证码邮件（自动生成验证码、定时等）
    */
    @Override
    public void sendCaptchaEmail(String toEmail, BaseEmailCaptchaDTO emailContentDTO, EmailSenderEnum senderEnum)
    {
        //检查是否发送
        checkSent(toEmail, senderEnum);
        doSendCaptcha(toEmail, senderEnum, emailContentDTO);
    }

    /**
    * 发送普通邮件
    */
    @Override
    public void sendEmail(BaseEmailContentDTO emailContentDTO, String toEmail, EmailSenderEnum senderEnum)
    {
        //检查是否发送
        checkSent(toEmail, senderEnum);
        emailSenderManager.doSendBySync(senderEnum, toEmail, emailContentDTO);
    }

    /**
     * Deprecated, please use sendCaptchaEmail/sendEmail instead.
     */
    @Deprecated
    @Override
    public Boolean sendEmail(SendEmailRequest sendEmailRequest, EmailSenderEnum senderEnum, UserVO userInfo)
    {
        return false;
//        HashMap<String, Object> paramsMap = new HashMap<>();
//        log.info("senderEnum: {}", senderEnum);
//        // 根据发送类型进行不同的处理
//        switch (senderEnum)
//        {
//            case RESET_PASSWORD:
//                // 重置密码直接设置为当前用户的，不相信前端的值
//                sendEmailRequest.setToEmail(userInfo.getUserEmail());
//                log.info("重置密码，发送给用户：{}", userInfo.getUserEmail());
//                break;
//            case RESET_EMAIL:
//                // 检查新旧邮箱是否一致
//                if (userInfo.getUserEmail() != null && sendEmailRequest.getToEmail().equals(userInfo.getUserEmail()))
//                {
//                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "新旧邮箱一致，无需修改哦");
//                }
//                break;
//            case ACTIVE_USER:
//                Map<String, Object> extractParams = sendEmailRequest.getExtractParams();
//                String accessToken = String.valueOf(extractParams.get("accessToken"));
//                String cacheToken = redisManager.getString(RedisKeyEnum.TEMP_REGISTER_TOKEN, sendEmailRequest.getToEmail());
//                ThrowUtils.throwIf(!cacheToken.equals(accessToken), ErrorCode.PARAMS_ERROR, "注册激活请求已失效，请重新注册");
//                paramsMap.put("token", accessToken);
//                break;
//            default:
//                throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        return doSendCaptcha(sendEmailRequest, senderEnum, paramsMap);
    }

    /**
     * 校验验证码
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/30 2:32
     */
    @Override
    public void verifyCaptcha(EmailSenderEnum emailSenderEnum, String toEmail, String code)
    {
        if (StringUtils.isAnyBlank(toEmail, code))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱或验证码为空");
        }
        // 从Redis中获取验证码
        HashMap<String, Object> captchaInfoMap = redisManager.getHashMap(emailSenderEnum, toEmail);
        if (captchaInfoMap == null || captchaInfoMap.isEmpty())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码已过期，请重新获取");
        }
        Optional.ofNullable(captchaInfoMap.get(EmailCaptchaConstant.CACHE_KEY_CODE))
                .map(String::valueOf)
                .filter(captcha -> captcha.equals(code))
                .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误"));

    }

    /**
     * 发送验证码统一方法
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/10 下午7:11
     */
    private void doSendCaptcha(String toEmail, EmailSenderEnum senderEnum, @NotNull BaseEmailCaptchaDTO captchaContentDTO)
    {
        // 检查目标邮箱是否为空
        ThrowUtils.throwIf(StringUtils.isBlank(toEmail), ErrorCode.PARAMS_ERROR);
        // 生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 设置redis缓存信息，验证码，邮箱信息
        captchaContentDTO.setCaptcha(code);
        // 将需要发送的邮箱账号写入redis和session，key为业务枚举值，后续不再相信前端上传的关于该邮箱的任何值，防止中间攻击。
        ServletUtils.setAttributeInSession(senderEnum.getKey(), toEmail);
        // 将验证码存入Redis，设置过期时间为5分钟
        redisManager.setHashMap(senderEnum, Map.of(EmailCaptchaConstant.CACHE_KEY_CODE, code), toEmail);
        // 异步发送邮件时，上层调用不关心发送是否成功
        emailSenderManager.doSendBySync(senderEnum, toEmail, captchaContentDTO);
    }


    /**
     * 检查是否发送
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/10 下午7:12
     */
    private void checkSent(String toEmail, EmailSenderEnum senderEnum)
    {
        // 检查目标邮箱
        ThrowUtils.throwIf(StringUtils.isBlank(toEmail), ErrorCode.PARAMS_ERROR, "邮箱不得为空");
        // 检查是否重复发送
        boolean hasSend = redisManager.setIfAbsent(senderEnum,  "1", toEmail);
        if (!hasSend)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮件已发送，请到邮箱内查收。");
        }
    }
}
