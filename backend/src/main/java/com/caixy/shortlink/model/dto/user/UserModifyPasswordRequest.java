package com.caixy.shortlink.model.dto.user;

import com.caixy.shortlink.constant.RegexPatternConstants;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改密码请求
 *
 * @name: com.caixy.shortlink.model.dto.user.UserModifyPasswordRequest
 * @author: CAIXYPROMISE
 * @since: 2024-04-14 20:42
 **/
@Data
public class UserModifyPasswordRequest implements Serializable
{
    @NotEmpty
    private String captchaCode;


    /**
     * 新密码
     */
    @Size(min = 8, max = 20, message = "密码长度必须在 8-20 位之间")
    @Pattern(regexp = RegexPatternConstants.PASSWORD_REGEX, message = "密码格式不正确")
    private String newPassword;

    /**
     * 确定密码
     */

    @Size(min = 8, max = 20, message = "密码长度必须在 8-20 位之间")
    @Pattern(regexp = RegexPatternConstants.PASSWORD_REGEX, message = "密码格式不正确")
    private String confirmPassword;

    @Serial
    private static final long serialVersionUID = 1L;
}
