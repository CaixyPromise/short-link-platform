package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.link.LinkAddRequest;
import com.caixy.shortlink.model.dto.link.LinkQueryRequest;
import com.caixy.shortlink.model.entity.Link;
import com.caixy.shortlink.model.vo.link.LinkCreateVO;
import com.caixy.shortlink.model.vo.link.LinkVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接信息服务
 * @author: CAIXYPROMISE
*/
public interface LinkService extends IService<Link> {

    /**
     * 校验数据
     *
     * @param link
     * @param add 对创建的数据进行校验
     */
    void validLink(Link link, boolean add);

    LinkCreateVO addShortLinkFormWeb(LinkAddRequest linkAddRequest);

    Page<LinkVO> getLinkVOPage(LinkQueryRequest linkQueryRequest, String nickName);

    /**
     * 获取短链接信息封装
     *
     * @param link
     * @param request
     * @return
     */
    LinkVO getLinkVO(Link link, HttpServletRequest request);

}
