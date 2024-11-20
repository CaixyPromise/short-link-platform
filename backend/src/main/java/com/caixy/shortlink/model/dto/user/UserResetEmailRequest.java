package com.caixy.shortlink.model.dto.user;

import com.caixy.shortlink.common.BaseSerializablePayload;
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
@Getter
@Setter
public class UserResetEmailRequest extends BaseSerializablePayload
{
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
}
