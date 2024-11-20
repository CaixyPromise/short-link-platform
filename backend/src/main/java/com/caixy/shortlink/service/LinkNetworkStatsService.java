package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkNetworkStats.LinkNetworkStatsQueryRequest;
import com.caixy.shortlink.model.entity.LinkNetworkStats;
import com.caixy.shortlink.model.vo.linkNetworkStats.LinkNetworkStatsVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接网络统计服务
 * @author: CAIXYPROMISE
*/
public interface LinkNetworkStatsService extends IService<LinkNetworkStats> {

    /**
     * 校验数据
     *
     * @param linkNetworkStats
     * @param add 对创建的数据进行校验
     */
    void validLinkNetworkStats(LinkNetworkStats linkNetworkStats, boolean add);

    /**
     * 获取查询条件
     *
     * @param linkNetworkStatsQueryRequest
     * @return
     */
    QueryWrapper<LinkNetworkStats> getQueryWrapper(LinkNetworkStatsQueryRequest linkNetworkStatsQueryRequest);
    
    /**
     * 获取短链接网络统计封装
     *
     * @param linkNetworkStats
     * @param request
     * @return
     */
    LinkNetworkStatsVO getLinkNetworkStatsVO(LinkNetworkStats linkNetworkStats, HttpServletRequest request);

    /**
     * 分页获取短链接网络统计封装
     *
     * @param linkNetworkStatsPage
     * @param request
     * @return
     */
    Page<LinkNetworkStatsVO> getLinkNetworkStatsVOPage(Page<LinkNetworkStats> linkNetworkStatsPage, HttpServletRequest request);
}
