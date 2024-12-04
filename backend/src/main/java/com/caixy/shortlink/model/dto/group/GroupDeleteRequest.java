package com.caixy.shortlink.model.dto.group;

import com.caixy.shortlink.common.BaseSerializablePayload;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 删除分组请求
 *
 * @Author CAIXYPROMISE
 * @since 2024/12/4 3:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupDeleteRequest extends BaseSerializablePayload
{
    /**
    * 分组id
    */
    @NotBlank
    private String gid;

    /**
    * 新分组id
    */
    private String newGroupId;
}
