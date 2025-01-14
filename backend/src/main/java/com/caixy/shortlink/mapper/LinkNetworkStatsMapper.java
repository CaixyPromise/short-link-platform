package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkGroupStatsReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkStatsReqDTO;
import com.caixy.shortlink.model.entity.LinkNetworkStats;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【t_link_network_stats(网络统计表)】的数据库操作Mapper
* @createDate 2024-11-10 00:44:34
* @Entity com.caixy.shortlink.model.entity.LinkNetworkStats
*/
public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStats> {
    /**
     * 记录访问网络监控数据
     */
    void shortLinkNetworkState(@Param("linkNetworkStats") LinkNetworkStats linkNetworkStats);

    /**
     * 根据短链接获取指定日期内访问网络监控数据
     */
    List<LinkNetworkStats> listNetworkStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内访问网络监控数据
     */
    List<LinkNetworkStats> listNetworkStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}




