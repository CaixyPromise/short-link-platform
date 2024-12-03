package com.caixy.shortlink.model.dto.group;

import com.caixy.shortlink.common.BaseSerializablePayload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新分组信息请求
 *


 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupUpdateRequest extends BaseSerializablePayload
{
    /**
     * gid
     */
    private String gid;

    /**
    * 分组名称
    */
    @Size(max = 16, min = 1, message = "分组名称长度在1-16之间")
    private String name;

    /**
    * 分组描述
    */
    @Size(max = 100, min = 1, message = "分组描述长度在1-100之间")
    private String description;

    /**
     * 分组权重
     */
    @Min(0)
    @Max(Integer.MAX_VALUE - 1)
    private Integer sortOrder;
}