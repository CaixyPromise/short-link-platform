package com.caixy.shortlink.model.dto.email;

import com.caixy.shortlink.common.BaseSerializablePayload;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 发送邮件请求体
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.model.dto.email.SendEmailRequest
 * @since 2024/10/10 下午4:43
 */
@Getter
@Setter
@Builder
public class SendEmailRequest extends BaseSerializablePayload
{
    /**
     * 目标邮箱
     */
    private String toEmail;

    /**
     * 场景
     */
    private Integer scenes;

    /**
     * 额外参数
     */
    private Map<String, Object> extractParams;

    private static final long serialVersionUID = 1L;
}
