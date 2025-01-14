package com.caixy.shortlink.model.dto.linkAccessStats;

import lombok.Data;

/**
 * 短链接监控请求参数
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 15:25
 */
@Data
public class ShortLinkStatsReqDTO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 启用状态
     */
    private Integer enableStatus;
}

