package com.caixy.shortlink.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.caixy.shortlink.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件引用表
 * @TableName t_file_reference
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_file_reference")
@Data
@Builder
public class FileReference extends BaseEntity implements Serializable  {

    /**
     * 关联文件ID
     */
    private Long fileId;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 展示名称
     */
    private String displayName;

    /**
     * 文件业务类型
     */
    private String bizType;

    /**
     * 文件关联业务的字段 ID
     */
    private Long bizId;

    /**
     * 访问权限级别（0私有，1登录可见，2公开）
     */
    private Integer accessLevel;


    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}