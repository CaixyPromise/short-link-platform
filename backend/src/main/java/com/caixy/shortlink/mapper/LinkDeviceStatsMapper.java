package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkGroupStatsReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkStatsReqDTO;
import com.caixy.shortlink.model.entity.LinkDeviceStats;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【t_link_device_stats(设备统计表)】的数据库操作Mapper
* @createDate 2024-11-10 00:44:34
* @Entity com.caixy.shortlink.model.entity.LinkDeviceStats
*/
public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStats> {

    /**
     * 记录访问设备监控数据
     */
    void shortLinkDeviceState(@Param("linkDeviceStats") LinkDeviceStats linkDeviceStats);

    /**
     * 根据短链接获取指定日期内访问设备监控数据
     */
    List<LinkDeviceStats> listDeviceStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内访问设备监控数据
     */
    List<LinkDeviceStats> listDeviceStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}




