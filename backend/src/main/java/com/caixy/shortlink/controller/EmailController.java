package com.caixy.shortlink.controller;

import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.manager.authorization.AuthManager;
import com.caixy.shortlink.manager.email.models.EmailSenderEnum;
import com.caixy.shortlink.model.dto.email.SendEmailRequest;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * Email发送接口控制器
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.controller.EmailController
 * @since 2024/10/10 下午4:31
 */
@Slf4j
@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController
{
    private final EmailService emailService;

    private final AuthManager authManager;

    /**
     * 发送邮件信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/10 下午5:49
     */
    @PostMapping("/send")
    public Result<Boolean> sendEmail(@RequestBody SendEmailRequest sendEmailRequest, HttpServletRequest request)
    {
        // 无需校验邮箱
        Integer scenes = sendEmailRequest.getScenes();
        EmailSenderEnum senderEnum = EmailSenderEnum.getByCode(scenes);
        ThrowUtils.throwIf(senderEnum == null, ErrorCode.PARAMS_ERROR);
        UserVO userInfo = Optional.ofNullable(senderEnum.getRequireLogin())
                .filter(aBoolean -> aBoolean)
                .map(aBoolean -> authManager.getLoginUser())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        return ResultUtils.success(emailService.sendEmail(sendEmailRequest, senderEnum, userInfo));
    }
}
