package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.dto.link.LinkAddRequest;
import com.caixy.shortlink.model.dto.link.LinkQueryRequest;
import com.caixy.shortlink.model.dto.link.LinkUpdateValidDateRequest;
import com.caixy.shortlink.model.entity.Link;
import com.caixy.shortlink.model.vo.link.LinkCreateVO;
import com.caixy.shortlink.model.vo.link.LinkVO;

import com.caixy.shortlink.model.vo.user.UserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接信息服务
 * @author: CAIXYPROMISE
*/
public interface LinkService extends IService<Link> {

    String redirectShortLink(String shortUrl, String shortUri);

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

    Boolean toggleLinkStatus(Long linkId, String groupId, UserVO loginUser, Integer status);

    Boolean updateLinkValidDate(LinkUpdateValidDateRequest linkUpdateValidDateRequest, UserVO userVO);
}
