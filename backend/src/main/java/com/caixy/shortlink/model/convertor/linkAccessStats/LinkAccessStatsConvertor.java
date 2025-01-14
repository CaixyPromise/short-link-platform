package com.caixy.shortlink.model.convertor.linkAccessStats;

import com.caixy.shortlink.model.entity.LinkAccessLogs;
import com.caixy.shortlink.model.vo.linkAccessStats.ShortLinkStatsAccessRecordRespDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 链接记录转换器
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 21:19
 */
@Mapper
public interface LinkAccessStatsConvertor
{
    LinkAccessStatsConvertor INSTANCE = Mappers.getMapper(LinkAccessStatsConvertor.class);

    ShortLinkStatsAccessRecordRespDTO AccessLogToRecordDto(LinkAccessLogs linkAccessLogs);
}
