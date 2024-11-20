package com.caixy.shortlink.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.mapper.LinkGotoMapper;
import com.caixy.shortlink.mapper.LinkMapper;
import com.caixy.shortlink.model.convertor.link.LinkConvertor;
import com.caixy.shortlink.model.dto.link.LinkAddRequest;
import com.caixy.shortlink.model.dto.link.LinkQueryRequest;
import com.caixy.shortlink.model.entity.Link;

import com.caixy.shortlink.model.entity.LinkGoto;
import com.caixy.shortlink.model.enums.ShortLinkDateType;
import com.caixy.shortlink.model.vo.link.LinkCreateVO;
import com.caixy.shortlink.model.vo.link.LinkVO;

import com.caixy.shortlink.service.LinkService;
import com.caixy.shortlink.utils.DateUtils;
import com.caixy.shortlink.utils.HashUtil;
import com.caixy.shortlink.utils.RegexUtils;
import com.caixy.shortlink.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.mapstruct.ap.internal.gem.SubclassMappingsGem.build;

/**
 * 短链接信息服务实现
 *
 * @author: CAIXYPROMISE
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService, InitializingBean
{
    private final RedissonClient redissonClient;
    private final LinkGotoMapper linkGotoMapper;
    private static final LinkConvertor linkConvertor = LinkConvertor.INSTANCE;

    private RBloomFilter<String> shortLinkFilter;
    private final static int MAX_RETRY_GENERATE_SHORT_LINK = 10;

    @Value("${short-link.default.domain}")
    private String defaultDomain;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.shortLinkFilter = redissonClient.getBloomFilter("shortLinkFilter");
        if (shortLinkFilter != null && !shortLinkFilter.isExists())
        {
            this.shortLinkFilter.tryInit(100000000L, 0.01);
        }
    }

    /**
     * 校验数据
     *
     * @param link
     * @param add  对创建的数据进行校验
     */
    @Override
    public void validLink(Link link, boolean add)
    {
        ThrowUtils.throwIf(link == null, ErrorCode.PARAMS_ERROR);
        String originUrl = link.getOriginUrl();
        ThrowUtils.throwIf(StringUtils.isBlank(originUrl) || !RegexUtils.validUrl(link.getOriginUrl()),
                ErrorCode.PARAMS_ERROR, "原始链接格式错误");
        ThrowUtils.throwIf(StringUtils.isBlank(link.getDescription()), ErrorCode.PARAMS_ERROR, "描述不能为空");
        ShortLinkDateType dateType = ShortLinkDateType.getEnumByCode(link.getValidDateType());
        ThrowUtils.throwIf(dateType == null || dateType.equals(ShortLinkDateType.CUSTOM) &&
                                               DateUtils.isBeforeNow(link.getValidDate()), ErrorCode.PARAMS_ERROR,
                "有效日期类型错误");
        ThrowUtils.throwIf(StringUtils.isBlank(link.getGid()), ErrorCode.PARAMS_ERROR, "分组id不能为空");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LinkCreateVO addShortLink(LinkAddRequest linkAddRequest)
    {
        String shortLinkSuffix = generateShortLinkSuffix(linkAddRequest);
        String finalShortLink = StrBuilder.create()
                                          .append(defaultDomain)
                                          .append(CommonConstant.URL_SUFFIX_SEPARATOR)
                                          .append(shortLinkSuffix)
                                          .toString();
        Link link = Link.builder()
                        .domain(defaultDomain)
                        .linkName(linkAddRequest.getLinkName())
                        .gid(linkAddRequest.getGid())
                        .fullShortUrl(finalShortLink)
                        .shortUri(shortLinkSuffix)
                        .favicon(HttpUtils.getFavicon(linkAddRequest.getOriginUrl()))
                        .description(linkAddRequest.getDescribe())
                        .originUrl(linkAddRequest.getOriginUrl())
                        .validDateType(linkAddRequest.getValidDateType())
                        .validDate(linkAddRequest.getValidDate())
                        .createdType(linkAddRequest.getCreatedType())
                        .enableStatus(0)
                        .clickNum(0)
                        .totalPv(0)
                        .totalUip(0)
                        .totalUv(0)
                        .build();
        validLink(link, true);
        LinkGoto linkGoto = LinkGoto.builder()
                                    .gid(linkAddRequest.getGid())
                                    .fullShortUrl(finalShortLink)
                                    .build();
        try
        {
            baseMapper.insert(link);
            linkGotoMapper.insert(linkGoto);
        }
        catch (DuplicateKeyException exception)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "短链接已存在");
        }
        // 添加进布隆过滤器
        shortLinkFilter.add(shortLinkSuffix);
        return LinkCreateVO.builder()
                           .gid(linkAddRequest.getGid())
                           .shortLink(finalShortLink)
                           .originUrl(linkAddRequest.getOriginUrl())
                           .build();
    }

    /**
     * 生成短链接后缀
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/18 1:48
     */
    private String generateShortLinkSuffix(LinkAddRequest linkAddRequest)
    {
        int retryCount = 0;
        String shortLinkSuffix = null;
        do
        {
            shortLinkSuffix = HashUtil.doHashToBase62(
                    linkAddRequest.getOriginUrl() + String.format("%s%s%s",
                            System.currentTimeMillis(),
                            UUID.randomUUID(),
                            RandomUtil.randomString(5)));
        } while (shortLinkSuffixExist(defaultDomain
                                      + CommonConstant.URL_SUFFIX_SEPARATOR
                                      + shortLinkSuffix)
                 && ++retryCount < MAX_RETRY_GENERATE_SHORT_LINK);
        if (retryCount >= MAX_RETRY_GENERATE_SHORT_LINK)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成短链接过于频繁，请稍后再试");
        }
        return shortLinkSuffix;
    }

    /**
     * 判断后缀是否存在
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/18 1:47
     */
    private boolean shortLinkSuffixExist(String suffix)
    {
        // 如果布隆过滤器内发现存在，去数据库确认一遍
        if (StringUtils.isNotBlank(suffix) && shortLinkFilter.contains(suffix))
        {
            return this.baseMapper.findShortLinkBySuffix(suffix) != null;
        }
        return false;
    }

    @Override
    public Page<LinkVO> getLinkVOPage(LinkQueryRequest linkQueryRequest, String nickName)
    {
        Page<Link> linkPage = new Page<>(linkQueryRequest.getCurrent(), linkQueryRequest.getPageSize());
        IPage<Link> linkIPage = baseMapper.queryLinksByGidAndNickName(linkPage, linkQueryRequest.getGid(), nickName);
        Page<LinkVO> linkVOPage = new Page<>(linkIPage.getCurrent(), linkIPage.getSize());
        linkVOPage.setTotal(linkIPage.getTotal());
        linkVOPage.setRecords(linkConvertor.toVOList(linkIPage.getRecords()));
        return linkVOPage;
    }

    /**
     * 获取短链接信息封装
     *
     * @param link
     * @param request
     * @return
     */
    @Override
    public LinkVO getLinkVO(Link link, HttpServletRequest request)
    {
        // todo: 补充获取短链接信息封装逻辑
        return null;
    }

}
