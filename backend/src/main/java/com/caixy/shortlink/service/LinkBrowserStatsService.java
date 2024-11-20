package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkBrowserStats.LinkBrowserStatsQueryRequest;
import com.caixy.shortlink.model.entity.LinkBrowserStats;
import com.caixy.shortlink.model.vo.linkBrowserStats.LinkBrowserStatsVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接浏览器统计服务
 * @author: CAIXYPROMISE
*/
public interface LinkBrowserStatsService extends IService<LinkBrowserStats> {

    /**
     * 校验数据
     *
     * @param linkBrowserStats
     * @param add 对创建的数据进行校验
     */
    void validLinkBrowserStats(LinkBrowserStats linkBrowserStats, boolean add);

    /**
     * 获取查询条件
     *
     * @param linkBrowserStatsQueryRequest
     * @return
     */
    QueryWrapper<LinkBrowserStats> getQueryWrapper(LinkBrowserStatsQueryRequest linkBrowserStatsQueryRequest);
    
    /**
     * 获取短链接浏览器统计封装
     *
     * @param linkBrowserStats
     * @param request
     * @return
     */
    LinkBrowserStatsVO getLinkBrowserStatsVO(LinkBrowserStats linkBrowserStats, HttpServletRequest request);

    /**
     * 分页获取短链接浏览器统计封装
     *
     * @param linkBrowserStatsPage
     * @param request
     * @return
     */
    Page<LinkBrowserStatsVO> getLinkBrowserStatsVOPage(Page<LinkBrowserStats> linkBrowserStatsPage, HttpServletRequest request);
}
