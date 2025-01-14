package com.caixy.shortlink.service.impl;


import cn.hutool.core.util.RandomUtil;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.manager.Email.EmailSenderManager;
import com.caixy.shortlink.manager.Email.core.EmailSenderDTO;
import com.caixy.shortlink.manager.Email.core.EmailSenderEnum;
import com.caixy.shortlink.manager.Email.models.captcha.EmailCaptchaConstant;
import com.caixy.shortlink.model.dto.email.SendEmailRequest;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.EmailService;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.utils.ServletUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.caixy.shortlink.utils.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;

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

    @Override
    public Boolean sendEmail(SendEmailRequest sendEmailRequest, EmailSenderEnum senderEnum, HttpServletRequest request,
                             UserVO userInfo)
    {
        HashMap<String, Object> paramsMap = new HashMap<>();
        log.info("senderEnum: {}", senderEnum);
        // 根据发送类型进行不同的处理
        switch (senderEnum)
        {
            case RESET_PASSWORD:
                // 重置密码直接设置为当前用户的，不相信前端的值
                sendEmailRequest.setToEmail(userInfo.getUserEmail());
                log.info("重置密码，发送给用户：{}", userInfo.getUserEmail());
                break;
            case RESET_EMAIL:
                // 检查新旧邮箱是否一致
                if (userInfo.getUserEmail() != null && sendEmailRequest.getToEmail().equals(userInfo.getUserEmail()))
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "新旧邮箱一致，无需修改哦");
                }
                break;
            case REGISTER:
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return doSendCaptcha(sendEmailRequest, senderEnum, paramsMap);
    }

    /**
     * 发送验证码统一方法
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/10 下午7:11
     */
    private Boolean doSendCaptcha(SendEmailRequest sendEmailRequest, EmailSenderEnum senderEnum,
                                  HashMap<String, Object> paramsMap)
    {
        // 获取目标邮箱
        String toEmail = sendEmailRequest.getToEmail();
        // 检查目标邮箱是否为空
        ThrowUtils.throwIf(StringUtils.isBlank(toEmail), ErrorCode.PARAMS_ERROR);
        //检查是否发送
        checkHasSend(toEmail, senderEnum);
        // 检查session是否发过同类型邮件
        Boolean hasAttributeInSession = ServletUtils.hasAttributeInSession(senderEnum.getKey());
        boolean hasSendKey = redisManager.hasKey(senderEnum, toEmail);
        ThrowUtils.throwIf(hasSendKey && hasAttributeInSession, ErrorCode.PARAMS_ERROR, "邮件已发送，请到邮箱内查收。");
        // 生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 设置redis缓存信息，验证码，邮箱信息
        paramsMap.put(EmailCaptchaConstant.CACHE_KEY_CODE, code);
        // 将需要发送的邮箱账号写入redis和session，key为业务枚举值，后续不再相信前端上传的关于该邮箱的任何值，防止中间攻击。
        ServletUtils.setAttributeInSession(senderEnum.getKey(), toEmail);
        // 将验证码存入Redis，设置过期时间为5分钟
        redisManager.setHashMap(senderEnum, paramsMap, toEmail);
        // 异步发送邮件时，上层调用不关心发送是否成功，已配置默认线程池失败策略为丢弃消息
        emailSenderManager.doSendBySync(senderEnum, new EmailSenderDTO(toEmail), paramsMap);
        return true;
    }


    /**
     * 检查是否发送
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/10 下午7:12
     */
    private void checkHasSend(String toEmail, EmailSenderEnum senderEnum)
    {
        // 检查目标邮箱
        ThrowUtils.throwIf(StringUtils.isBlank(toEmail), ErrorCode.PARAMS_ERROR, "邮箱不得为空");
        // 检查是否重复发送
        boolean hasSend = redisManager.hasKey(senderEnum, toEmail);
        if (hasSend)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮件已发送，请到邮箱内查收。");
        }
    }
}
