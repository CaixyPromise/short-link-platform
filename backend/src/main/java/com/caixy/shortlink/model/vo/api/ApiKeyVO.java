package com.caixy.shortlink.model.vo.api;

import com.caixy.shortlink.common.BaseSerializablePayload;
import lombok.*;

/**
 * API-key结果视图
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/14 21:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyVO extends BaseSerializablePayload
{
    /**
    * API-key
    */
    private String accessKey;
    /**
    * secretKey
    */
    private String secretKey;
}
