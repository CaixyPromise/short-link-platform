package com.caixy.shortlink.model.dto.group;

import com.caixy.shortlink.common.BaseSerializablePayload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 创建分组信息请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupAddRequest extends BaseSerializablePayload
{
    /**
    * 分组名称
    */
    @NotNull
    @Size(max = 16, min = 1, message = "分组名称长度在1-16之间")
    private String groupName;

    /**
    * 分组描述
    */
    @Size(max = 100, min = 1, message = "分组描述长度在1-100之间")
    private String description;
}