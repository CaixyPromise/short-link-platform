package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkDeviceStats.LinkDeviceStatsQueryRequest;
import com.caixy.shortlink.model.entity.LinkDeviceStats;
import com.caixy.shortlink.model.vo.linkDeviceStats.LinkDeviceStatsVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接设备统计服务
 * @author: CAIXYPROMISE
*/
public interface LinkDeviceStatsService extends IService<LinkDeviceStats> {

    /**
     * 校验数据
     *
     * @param linkDeviceStats
     * @param add 对创建的数据进行校验
     */
    void validLinkDeviceStats(LinkDeviceStats linkDeviceStats, boolean add);

    /**
     * 获取查询条件
     *
     * @param linkDeviceStatsQueryRequest
     * @return
     */
    QueryWrapper<LinkDeviceStats> getQueryWrapper(LinkDeviceStatsQueryRequest linkDeviceStatsQueryRequest);
    
    /**
     * 获取短链接设备统计封装
     *
     * @param linkDeviceStats
     * @param request
     * @return
     */
    LinkDeviceStatsVO getLinkDeviceStatsVO(LinkDeviceStats linkDeviceStats, HttpServletRequest request);

    /**
     * 分页获取短链接设备统计封装
     *
     * @param linkDeviceStatsPage
     * @param request
     * @return
     */
    Page<LinkDeviceStatsVO> getLinkDeviceStatsVOPage(Page<LinkDeviceStats> linkDeviceStatsPage, HttpServletRequest request);
}
