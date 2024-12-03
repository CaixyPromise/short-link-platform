package com.caixy.shortlink.model.vo.group;

import com.caixy.shortlink.common.BaseSerializablePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

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
    /**
    * 组id
    */
    private String gid;
    /**
    * 分组名称
    */
    private String name;
    /**
    * 链接数量
    */
    private int linkCount;
}
