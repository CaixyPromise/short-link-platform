package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkAccessLogs.LinkAccessLogsQueryRequest;
import com.caixy.shortlink.model.entity.LinkAccessLogs;
import com.caixy.shortlink.model.vo.linkAccessLogs.LinkAccessLogsVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接访问日志服务
 * @author: CAIXYPROMISE
*/
public interface LinkAccessLogsService extends IService<LinkAccessLogs> {

    /**
     * 校验数据
     *
     * @param linkAccessLogs
     * @param add 对创建的数据进行校验
     */
    void validLinkAccessLogs(LinkAccessLogs linkAccessLogs, boolean add);

    /**
     * 获取查询条件
     *
     * @param linkAccessLogsQueryRequest
     * @return
     */
    QueryWrapper<LinkAccessLogs> getQueryWrapper(LinkAccessLogsQueryRequest linkAccessLogsQueryRequest);
    
    /**
     * 获取短链接访问日志封装
     *
     * @param linkAccessLogs
     * @param request
     * @return
     */
    LinkAccessLogsVO getLinkAccessLogsVO(LinkAccessLogs linkAccessLogs, HttpServletRequest request);

    /**
     * 分页获取短链接访问日志封装
     *
     * @param linkAccessLogsPage
     * @param request
     * @return
     */
    Page<LinkAccessLogsVO> getLinkAccessLogsVOPage(Page<LinkAccessLogs> linkAccessLogsPage, HttpServletRequest request);
}
