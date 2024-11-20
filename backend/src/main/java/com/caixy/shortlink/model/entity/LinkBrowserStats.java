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
 * 浏览器统计表
 * @TableName t_link_browser_stats
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_link_browser_stats")
@Data
public class LinkBrowserStats extends BaseEntity implements Serializable {
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
    private Integer cnt;

    /**
     * 浏览器
     */
    private String browser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}