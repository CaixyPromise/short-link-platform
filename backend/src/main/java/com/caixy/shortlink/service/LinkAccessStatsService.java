package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkGroupStatsAccessRecordReqDTO;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkStatsAccessRecordReqDTO;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkStatsRecordDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkGroupStatsReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkStatsReqDTO;
import com.caixy.shortlink.model.entity.LinkAccessStats;

import com.caixy.shortlink.model.vo.linkAccessStats.ShortLinkStatsAccessRecordRespDTO;
import com.caixy.shortlink.model.vo.linkAccessStats.ShortLinkStatsRespDTO;
import org.springframework.transaction.annotation.Transactional;

/**
 * 短链接访问统计服务
 *
 * @author: CAIXYPROMISE
 */
public interface LinkAccessStatsService extends IService<LinkAccessStats>
{

    /**
     * 保存短链接访问统计记录
     *
     * @param statsRecord 统计记录
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/13 21:16
     */
    @Transactional(rollbackFor = Exception.class)
    void saveShortLinkStats(ShortLinkStatsRecordDTO statsRecord);

    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/13 21:16
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 获取分组短链接监控数据
     *
     * @param requestParam 获取分组短链接监控数据入参
     * @return 分组短链接监控数据
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/13 21:17
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 获取短链接监控访问记录数据入参
     * @return 访问记录监控数据
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/13 21:17
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(
            ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     * @param requestParam 获取分组短链接监控访问记录数据入参
     * @return 分组访问记录监控数据
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/13 21:17
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(
            ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
