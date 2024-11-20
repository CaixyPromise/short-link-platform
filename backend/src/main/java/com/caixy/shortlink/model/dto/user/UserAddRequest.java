package com.caixy.shortlink.model.dto.user;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 用户创建请求
 */
@Data
public class UserAddRequest implements Serializable
{
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}