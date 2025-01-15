package com.caixy.shortlink.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.caixy.shortlink.common.BaseEntity;
import com.caixy.shortlink.handler.AesEncryptTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * API信息表
 * @TableName t_api_key
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_api_key", autoResultMap = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiKey extends BaseEntity
{
    /**
     * api_key
     */
    private String accessKey;

    /**
     * secret_key
     */
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String secretKey;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 最后请求时间
     */
    private Date lastRequestTime;

    /**
     * 最后刷新时间
     */
    private Date lastRefreshTime;
}