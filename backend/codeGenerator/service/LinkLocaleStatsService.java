package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkLocaleStats.LinkLocaleStatsQueryRequest;
import com.caixy.shortlink.model.entity.LinkLocaleStats;
import com.caixy.shortlink.model.vo.linkLocaleStats.LinkLocaleStatsVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接地域统计服务
 * @author: CAIXYPROMISE
*/
public interface LinkLocaleStatsService extends IService<LinkLocaleStats> {

    /**
     * 校验数据
     *
     * @param linkLocaleStats
     * @param add 对创建的数据进行校验
     */
    void validLinkLocaleStats(LinkLocaleStats linkLocaleStats, boolean add);

    /**
     * 获取查询条件
     *
     * @param linkLocaleStatsQueryRequest
     * @return
     */
    QueryWrapper<LinkLocaleStats> getQueryWrapper(LinkLocaleStatsQueryRequest linkLocaleStatsQueryRequest);
    
    /**
     * 获取短链接地域统计封装
     *
     * @param linkLocaleStats
     * @param request
     * @return
     */
    LinkLocaleStatsVO getLinkLocaleStatsVO(LinkLocaleStats linkLocaleStats, HttpServletRequest request);

    /**
     * 分页获取短链接地域统计封装
     *
     * @param linkLocaleStatsPage
     * @param request
     * @return
     */
    Page<LinkLocaleStatsVO> getLinkLocaleStatsVOPage(Page<LinkLocaleStats> linkLocaleStatsPage, HttpServletRequest request);
}
