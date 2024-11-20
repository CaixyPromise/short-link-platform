package com.caixy.shortlink.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.caixy.shortlink.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分组信息表
 *
 * @TableName t_group
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_group")
@Data
@Builder
public class Group extends BaseEntity implements Serializable
{
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组描述
     */
    private String description;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}