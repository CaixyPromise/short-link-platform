package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkStatsToday.LinkStatsTodayQueryRequest;
import com.caixy.shortlink.model.entity.LinkStatsToday;
import com.caixy.shortlink.model.vo.linkStatsToday.LinkStatsTodayVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接当日统计服务
 * @author: CAIXYPROMISE
*/
public interface LinkStatsTodayService extends IService<LinkStatsToday> {

    /**
     * 校验数据
     *
     * @param linkStatsToday
     * @param add 对创建的数据进行校验
     */
    void validLinkStatsToday(LinkStatsToday linkStatsToday, boolean add);

    /**
     * 获取查询条件
     *
     * @param linkStatsTodayQueryRequest
     * @return
     */
    QueryWrapper<LinkStatsToday> getQueryWrapper(LinkStatsTodayQueryRequest linkStatsTodayQueryRequest);
    
    /**
     * 获取短链接当日统计封装
     *
     * @param linkStatsToday
     * @param request
     * @return
     */
    LinkStatsTodayVO getLinkStatsTodayVO(LinkStatsToday linkStatsToday, HttpServletRequest request);

    /**
     * 分页获取短链接当日统计封装
     *
     * @param linkStatsTodayPage
     * @param request
     * @return
     */
    Page<LinkStatsTodayVO> getLinkStatsTodayVOPage(Page<LinkStatsToday> linkStatsTodayPage, HttpServletRequest request);
}
