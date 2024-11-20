package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.linkGoto.LinkGotoQueryRequest;
import com.caixy.shortlink.model.entity.LinkGoto;
import com.caixy.shortlink.model.vo.linkGoto.LinkGotoVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接跳转信息服务
 * @author: CAIXYPROMISE
*/
public interface LinkGotoService extends IService<LinkGoto> {

    /**
     * 校验数据
     *
     * @param linkGoto
     * @param add 对创建的数据进行校验
     */
    void validLinkGoto(LinkGoto linkGoto, boolean add);

    /**
     * 获取查询条件
     *
     * @param linkGotoQueryRequest
     * @return
     */
    QueryWrapper<LinkGoto> getQueryWrapper(LinkGotoQueryRequest linkGotoQueryRequest);
    
    /**
     * 获取短链接跳转信息封装
     *
     * @param linkGoto
     * @param request
     * @return
     */
    LinkGotoVO getLinkGotoVO(LinkGoto linkGoto, HttpServletRequest request);

    /**
     * 分页获取短链接跳转信息封装
     *
     * @param linkGotoPage
     * @param request
     * @return
     */
    Page<LinkGotoVO> getLinkGotoVOPage(Page<LinkGoto> linkGotoPage, HttpServletRequest request);
}
