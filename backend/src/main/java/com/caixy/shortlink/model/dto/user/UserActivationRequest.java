package com.caixy.shortlink.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 *
 * @Author CAIXYPROMISE
 * @since 2025/4/23 18:08
 */
@Data
public class UserActivationRequest implements Serializable
{
    /**
    * 密码
    */
    private String password;
    /**
    * 确认密码
    */
    private String confirmPassword;
}
