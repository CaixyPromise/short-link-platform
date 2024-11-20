package com.caixy.shortlink.model.dto.user;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    /**
     * 旧密码
     */
    
    private String captchaCode;


    /**
     * 新密码
     */
    
    @Min(8)
    @Max(20)
    private String newPassword;

    /**
     * 确定密码
     */
    
    @Min(8)
    @Max(20)
    private String confirmPassword;

    private static final long serialVersionUID = 1L;
}
