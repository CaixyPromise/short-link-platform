package com.caixy.shortlink.model.dto.group;

import com.caixy.shortlink.common.BaseSerializablePayload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新分组order请求
 *
 * @Author CAIXYPROMISE
 * @since 2024/12/4 3:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupUpdateOrderRequest extends BaseSerializablePayload
{
    /**
    * 组id
    */
    @NotBlank
    private String gid;

    /**
    * 偏移量
    */
    @NotNull
    @Min(Integer.MIN_VALUE + 1)
    @Max(Integer.MAX_VALUE - 1)
    private Integer offset;
}
