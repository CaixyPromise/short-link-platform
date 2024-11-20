package com.caixy.shortlink.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.caixy.shortlink.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 短链接访问统计表
 * @TableName t_link_access_stats
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_link_access_stats")
@Data
public class LinkAccessStats extends BaseEntity implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer pv;

    /**
     * 独立访客数
     */
    private Integer uv;

    /**
     * 独立IP数
     */
    private Integer uip;

    /**
     * 小时
     */
    private Integer hour;

    /**
     * 星期
     */
    private Integer weekday;
    

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}