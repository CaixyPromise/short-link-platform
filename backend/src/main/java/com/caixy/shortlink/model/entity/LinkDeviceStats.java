package com.caixy.shortlink.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.caixy.shortlink.common.BaseEntity;
import lombok.*;

/**
 * 设备统计表
 * @TableName t_link_device_stats
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_link_device_stats")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkDeviceStats extends BaseEntity implements Serializable {

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
     * 访问设备
     */
    private String device;
}