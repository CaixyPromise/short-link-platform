package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkGroupStatsReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkStatsReqDTO;
import com.caixy.shortlink.model.entity.LinkAccessStats;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【t_link_access_stats(短链接访问统计表)】的数据库操作Mapper
* @createDate 2024-11-10 00:44:34
* @Entity com.caixy.shortlink.model.entity.LinkAccessStats
*/
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStats> {
    /**
     * 记录基础访问监控数据
     */
    void shortLinkStats(@Param("linkAccessStats") LinkAccessStats linkAccessStats);

    /**
     * 根据短链接获取指定日期内基础监控数据
     */
    List<LinkAccessStats> listStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内基础监控数据
     */
    List<LinkAccessStats> listStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    List<LinkAccessStats> listHourStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内小时基础监控数据
     */
    List<LinkAccessStats> listHourStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内按星期基础监控数据
     */
    List<LinkAccessStats> listWeekdayStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内按星期基础监控数据
     */
    List<LinkAccessStats> listWeekdayStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}




