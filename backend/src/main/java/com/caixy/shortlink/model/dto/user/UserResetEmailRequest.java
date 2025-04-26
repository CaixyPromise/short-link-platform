package com.caixy.shortlink.model.dto.user;

import com.caixy.shortlink.common.BaseSerializablePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 重置邮箱请求
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.model.dto.user.UserResetEmailRequest
 * @since 2024/10/10 下午9:03
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Data
public class UserResetEmailRequest extends BaseSerializablePayload
{
    /**
    * 原始邮箱
    */
    private String originalEmail;
    
    /**
    * 新邮箱
    */
    private String newEmail;
    
    /**
     * 邮箱验证码
     */
    
    @NotEmpty
    private String code;

    /**
     * 重置密码
     */
    
    @NotEmpty
    private String password;

    private String token;
}
