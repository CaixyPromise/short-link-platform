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
 * 操作系统统计表
 * @TableName t_link_os_stats
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_link_os_stats")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkOsStats extends BaseEntity {


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
     * 操作系统
     */
    private String os;

}