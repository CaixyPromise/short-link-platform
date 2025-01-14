package com.caixy.shortlink.model.dto.linkAccessLogs;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.shortlink.model.entity.LinkAccessLogs;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分组短链接监控访问记录请求参数
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 16:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShortLinkGroupStatsAccessRecordReqDTO extends Page<LinkAccessLogs>
{

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
