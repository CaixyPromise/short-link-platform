package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkAccessStats.LinkAccessStatsQueryRequest;
import com.caixy.shortlink.model.entity.LinkAccessStats;
import com.caixy.shortlink.model.vo.linkAccessStats.LinkAccessStatsVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接访问统计服务
 * @author: CAIXYPROMISE
*/
public interface LinkAccessStatsService extends IService<LinkAccessStats> {

    /**
     * 校验数据
     *
     * @param linkAccessStats
     * @param add 对创建的数据进行校验
     */
    void validLinkAccessStats(LinkAccessStats linkAccessStats, boolean add);

    /**
     * 获取查询条件
     *
     * @param linkAccessStatsQueryRequest
     * @return
     */
    QueryWrapper<LinkAccessStats> getQueryWrapper(LinkAccessStatsQueryRequest linkAccessStatsQueryRequest);
    
    /**
     * 获取短链接访问统计封装
     *
     * @param linkAccessStats
     * @param request
     * @return
     */
    LinkAccessStatsVO getLinkAccessStatsVO(LinkAccessStats linkAccessStats, HttpServletRequest request);

    /**
     * 分页获取短链接访问统计封装
     *
     * @param linkAccessStatsPage
     * @param request
     * @return
     */
    Page<LinkAccessStatsVO> getLinkAccessStatsVOPage(Page<LinkAccessStats> linkAccessStatsPage, HttpServletRequest request);
}
