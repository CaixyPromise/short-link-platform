package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkGroupStatsReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkStatsReqDTO;
import com.caixy.shortlink.model.entity.LinkOsStats;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【t_link_os_stats(操作系统统计表)】的数据库操作Mapper
* @createDate 2024-11-10 00:44:34
* @Entity com.caixy.shortlink.model.entity.LinkOsStats
*/
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStats> {

    /**
     * 记录操作系统访问监控数据
     */
    void shortLinkOsState(@Param("linkOsStats") LinkOsStats linkOsStatsDO);

    /**
     * 根据短链接获取指定日期内操作系统监控数据
     */
    List<HashMap<String, Object>> listOsStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内操作系统监控数据
     */
    List<HashMap<String, Object>> listOsStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}




