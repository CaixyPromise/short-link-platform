package com.caixy.shortlink.model.dto.group;

import com.caixy.shortlink.common.BaseSerializablePayload;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新分组信息请求
 *


 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupUpdateInfoRequest extends BaseSerializablePayload
{
    /**
     * gid
     */
    @NotBlank
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
}