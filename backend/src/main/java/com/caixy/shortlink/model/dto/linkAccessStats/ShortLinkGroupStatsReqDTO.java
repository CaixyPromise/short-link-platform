package com.caixy.shortlink.model.dto.linkAccessStats;

import lombok.Data;

/**
 * 分组短链接监控请求参数
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 15:22
 */
@Data
public class ShortLinkGroupStatsReqDTO {

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
}
