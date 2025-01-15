package com.caixy.shortlink.model.dto.api;

import com.caixy.shortlink.common.BaseSerializablePayload;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取API-key接口请求
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/14 21:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryApiKeyRequest extends BaseSerializablePayload
{
    /**
    * 前端生成的RSA公钥
    */
    @NotBlank
    private String publicKey;
}
