package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkGroupStatsReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkStatsReqDTO;
import com.caixy.shortlink.model.entity.LinkLocaleStats;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【t_link_locale_stats(地区统计表)】的数据库操作Mapper
* @createDate 2024-11-10 00:44:34
* @Entity com.caixy.shortlink.model.entity.LinkLocaleStats
*/
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStats> {
    /**
     * 记录地区访问监控数据
     */
    void shortLinkLocaleState(@Param("linkLocaleStats") LinkLocaleStats linkLocaleStats);

    /**
     * 根据短链接获取指定日期内地区监控数据
     */
    List<LinkLocaleStats> listLocaleByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内地区监控数据
     */
    List<LinkLocaleStats> listLocaleByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}




