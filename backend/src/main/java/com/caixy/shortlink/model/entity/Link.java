package com.caixy.shortlink.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.caixy.shortlink.common.BaseEntity;
import lombok.*;

/**
 * 短链接信息表
 * @TableName t_link
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_link")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Link extends BaseEntity implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 域名
     */
    private String domain;
    
    /**
    * 短链名称
    */
    private String linkName;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 网站图标
     */
    private String favicon;

    /**
     * 启用标识 0：启用 1：未启用
     */
    private Integer enableStatus;

    /**
     * 创建类型 0：接口创建 1：控制台创建
     */
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    private Integer validDateType;

    /**
     * 有效期-开始时间
     */
    private Date validDateStart;

    /**
    * 有效期-结束时间
    */
    private Date validDateEnd;

    /**
     * 描述
     */
    private String description;

    /**
     * 历史PV
     */
    private Integer totalPv;

    /**
     * 历史UV
     */
    private Integer totalUv;

    /**
     * 历史UIP
     */
    private Integer totalUip;


    /**
     * 删除时间戳
     */
    private Long delTime;
    

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}