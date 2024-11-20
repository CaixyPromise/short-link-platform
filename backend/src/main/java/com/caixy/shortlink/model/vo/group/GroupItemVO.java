package com.caixy.shortlink.model.vo.group;

import com.caixy.shortlink.common.BaseSerializablePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分组选项VO，用于侧边栏选项
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/21 0:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupItemVO extends BaseSerializablePayload
{
    private String gid;
    private String name;
    private int linkCount;
}
