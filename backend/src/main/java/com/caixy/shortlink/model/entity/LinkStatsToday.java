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
 * 当日统计表
 * @TableName t_link_stats_today
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_link_stats_today")
@Data
public class LinkStatsToday extends BaseEntity implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 今日PV
     */
    private Integer todayPv;

    /**
     * 今日UV
     */
    private Integer todayUv;

    /**
     * 今日IP数
     */
    private Integer todayUip;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}