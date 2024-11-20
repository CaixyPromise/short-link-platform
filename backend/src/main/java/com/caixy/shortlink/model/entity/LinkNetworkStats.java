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
 * 网络统计表
 * @TableName t_link_network_stats
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_link_network_stats")
@Data
public class LinkNetworkStats extends BaseEntity implements Serializable {
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
     * 访问网络
     */
    private String network;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}