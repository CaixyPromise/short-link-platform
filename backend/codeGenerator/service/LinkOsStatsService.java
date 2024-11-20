package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkOsStats.LinkOsStatsQueryRequest;
import com.caixy.shortlink.model.entity.LinkOsStats;
import com.caixy.shortlink.model.vo.linkOsStats.LinkOsStatsVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接操作系统统计服务
 * @author: CAIXYPROMISE
*/
public interface LinkOsStatsService extends IService<LinkOsStats> {

    /**
     * 校验数据
     *
     * @param linkOsStats
     * @param add 对创建的数据进行校验
     */
    void validLinkOsStats(LinkOsStats linkOsStats, boolean add);

    /**
     * 获取查询条件
     *
     * @param linkOsStatsQueryRequest
     * @return
     */
    QueryWrapper<LinkOsStats> getQueryWrapper(LinkOsStatsQueryRequest linkOsStatsQueryRequest);
    
    /**
     * 获取短链接操作系统统计封装
     *
     * @param linkOsStats
     * @param request
     * @return
     */
    LinkOsStatsVO getLinkOsStatsVO(LinkOsStats linkOsStats, HttpServletRequest request);

    /**
     * 分页获取短链接操作系统统计封装
     *
     * @param linkOsStatsPage
     * @param request
     * @return
     */
    Page<LinkOsStatsVO> getLinkOsStatsVOPage(Page<LinkOsStats> linkOsStatsPage, HttpServletRequest request);
}
