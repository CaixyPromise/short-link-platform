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
 * 地区统计表
 * @TableName t_link_locale_stats
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_link_locale_stats")
@Data
public class LinkLocaleStats extends BaseEntity implements Serializable {
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
     * 省份名称
     */
    private String province;

    /**
     * 市名称
     */
    private String city;

    /**
     * 城市编码
     */
    private String adcode;

    /**
     * 国家标识
     */
    private String country;
    

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}