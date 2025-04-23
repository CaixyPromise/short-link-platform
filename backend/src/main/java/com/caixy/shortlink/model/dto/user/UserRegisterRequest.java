package com.caixy.shortlink.model.dto.user;

import com.caixy.shortlink.constant.RegexPatternConstants;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable
{
    /**
     * 用户昵称
     */
    @Pattern(regexp = RegexPatternConstants.NAME_REGEX, message = "用户名必须为4到15位（只能包含中文字符和英文大小写字母）")
    private String userName;

    /**
     * 用户邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email
    private String userEmail;

    /**
    * 图像验证码
    */
    @NotBlank(message = "验证码不能为空")
    private String captcha;

    /**
    * 图像验证码Id
    */
    @NotBlank(message = "验证码Id不能为空")
    private String captchaId;

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;
}
