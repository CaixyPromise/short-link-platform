package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.entity.LinkStatsToday;
import org.apache.ibatis.annotations.Param;

/**
* @author CAIXYPROMISE
* @description 针对表【t_link_stats_today(当日统计表)】的数据库操作Mapper
* @createDate 2024-11-10 00:44:34
* @Entity com.caixy.shortlink.model.entity.LinkStatsToday
*/
public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsToday> {
    /**
     * 记录今日统计监控数据
     */
    void shortLinkTodayState(@Param("linkTodayStats") LinkStatsToday linkStatsTodayDO);
}




