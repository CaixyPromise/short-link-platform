package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkGroupStatsReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkStatsReqDTO;
import com.caixy.shortlink.model.entity.LinkBrowserStats;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【t_link_browser_stats(浏览器统计表)】的数据库操作Mapper
* @createDate 2024-11-10 00:44:34
* @Entity com.caixy.shortlink.model.entity.LinkBrowserStats
*/
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStats> {


    /**
     * 记录浏览器访问监控数据
     */
    void shortLinkBrowserState(@Param("linkBrowserStats") LinkBrowserStats linkBrowserStatsDO);

    /**
     * 根据短链接获取指定日期内浏览器监控数据
     */
    List<HashMap<String, Object>> listBrowserStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内浏览器监控数据
     */
    List<HashMap<String, Object>> listBrowserStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}




