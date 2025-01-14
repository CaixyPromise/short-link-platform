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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * 短链接信息服务
 * @author: CAIXYPROMISE
*/
public interface LinkService extends IService<Link> {

    /**
     * 跳转链接逻辑
     *
     * @param shortUrl 用户的访问的完整url
     * @param shortUri 用户的访问的uri (短链后缀)
     * @param request
     * @param response
     * @return 重定向的链接(校验和数据校验通过 ， 返回原链接 ， 反之返回notFound链接)
     */
    String redirectShortLink(String shortUrl, String shortUri, HttpServletRequest request, HttpServletResponse response);

    /**
     * 校验数据
     *
     * @param link
     * @param add  对创建的数据进行校验
     */
    void validLink(Link link, boolean add);

    /**
     * 从页面添加的短链接
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/22 17:55
     */
    @Transactional(rollbackFor = Exception.class)
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

    /**
     * 根据用户的链接更新有效期
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/29 3:21
     */
    Boolean updateLinkValidDate(LinkUpdateValidDateRequest linkUpdateValidDateRequest, UserVO userVO);

    /**
     * 批量根据链接将其迁移分组
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/29 14:18
     */
    @Transactional
    Boolean moveLinksToGroup(String groupId, String newGroupId, Collection<Long> linkIds, UserVO loginUser);
}
