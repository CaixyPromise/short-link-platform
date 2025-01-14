package com.caixy.shortlink.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.manager.RDLock.DistributedLockManager;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.mapper.GroupMapper;
import com.caixy.shortlink.mapper.LinkGotoMapper;
import com.caixy.shortlink.mapper.LinkMapper;
import com.caixy.shortlink.model.convertor.link.LinkConvertor;
import com.caixy.shortlink.model.dto.link.LinkAddRequest;
import com.caixy.shortlink.model.dto.link.LinkQueryRequest;
import com.caixy.shortlink.model.dto.link.LinkUpdateValidDateRequest;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkStatsRecordDTO;
import com.caixy.shortlink.model.entity.Group;
import com.caixy.shortlink.model.entity.Link;

import com.caixy.shortlink.model.entity.LinkGoto;
import com.caixy.shortlink.model.enums.*;
import com.caixy.shortlink.model.vo.link.LinkCreateVO;
import com.caixy.shortlink.model.vo.link.LinkVO;

import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.mq.producer.ShortLinkStatsSaveProducer;
import com.caixy.shortlink.service.GroupService;
import com.caixy.shortlink.service.LinkService;
import com.caixy.shortlink.utils.*;
import com.caixy.shortlink.utils.http.HttpUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    private final GroupMapper groupMapper;
    private final DistributedLockManager distributedLockManager;
    private final RedisManager redisManager;
    private final GroupService groupService;
    private final ShortLinkStatsSaveProducer shortLinkStatsSaveProducer;

    private RBloomFilter<String> shortLinkFilter;

    private static final int MAX_RETRY_GENERATE_SHORT_LINK = 10;
    private static final LinkConvertor linkConvertor = LinkConvertor.INSTANCE;

    @Value("${short-link.default.domain}")
    private String defaultDomain;

    @Value("${short-link.default.notFound-uri}")
    private String notFoundUri;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.shortLinkFilter = redissonClient.getBloomFilter("shortLinkFilter");
        if (shortLinkFilter != null && !shortLinkFilter.isExists())
        {
            this.shortLinkFilter.tryInit(100000000L, 0.01);
        }
    }


    @Override
    public String redirectShortLink(String shortUrl, String shortUri, HttpServletRequest request, HttpServletResponse response)
    {
        // 1. 检查redis缓存是否存在目标链接
        String originUrl = getShortLinkFormCache(shortUrl);
        if (StringUtils.isNotBlank(originUrl))
        {
            shortLinkStatsSaveProducer.sendMessage(buildLinkStatsRecordAndSetUser(shortUrl, request, response));
            return originUrl;
        }
        // 2. 检查布隆过滤器是否存在目标链接
        if (!shortLinkSuffixExist(shortUri))
        {
            return notFoundUri;
        }
        // 3. 检查是否是无效链接
        if (isInvalidLink(shortUrl))
        {
            return notFoundUri;
        }
        // 4. 加分布式锁，查询数据库
        return distributedLockManager.redissonDistributedLocks(
                RDLockKeyEnum.SHORT_LINK_LOCK,
                () -> {
                    // 1. 再次检查有没有原始链接（并发情况下，可能缓存已经更新了原链接）
                    String originLinkInLambda = getShortLinkFormCache(shortUrl);
                    if (StringUtils.isNotBlank(originLinkInLambda))
                    {
                        shortLinkStatsSaveProducer.sendMessage(buildLinkStatsRecordAndSetUser(shortUrl, request, response));
                        return originLinkInLambda;
                    }
                    // 2. 检查是否是无效链接
                    if (isInvalidLink(shortUrl))
                    {
                        log.info("无效链接: {}", shortUrl);
                        return notFoundUri;
                    }
                    // 3. 查询数据库
                    // 3.1. 根据短链查询链接信息
                    LambdaQueryWrapper<LinkGoto> linkGotoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    linkGotoLambdaQueryWrapper.eq(LinkGoto::getFullShortUrl, shortUrl);
                    LinkGoto linkGoto = linkGotoMapper.selectOne(linkGotoLambdaQueryWrapper);
                    // 3.1.1 没找到，则直接返回notFound
                    if (linkGoto == null)
                    {
                        log.info("未找到链接信息: {}", shortUrl);
                        setInvalidLink(shortUrl);
                        return notFoundUri;
                    }
                    // 3.2. 查询链接完整信息
                    LambdaQueryWrapper<Link> linkLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    linkLambdaQueryWrapper
                            .eq(Link::getGid, linkGoto.getGid())
                            .eq(Link::getFullShortUrl, shortUrl)
                            .eq(Link::getIsDeleted, CommonConstant.NOT_DELETE_FLAG)
                            .eq(Link::getEnableStatus, CommonConstant.ENABLE_STATUS);
                    Link link = this.getOne(linkLambdaQueryWrapper);
                    // 3.2.1 没找到，则直接返回notFound
                    if (link == null)
                    {
                        setInvalidLink(shortUrl);
                        log.info("未找到链接完整信息: {}", shortUrl);
                        return notFoundUri;
                    }
                    // 3.2.2 校验有效期
                    if (!link.getValidDateType().equals(ShortLinkDateType.PERMANENT.getCode()) &&
                        (DateUtils.isBeforeNow(link.getValidDateEnd()) ||
                         DateUtils.isAfterNow(link.getValidDateStart())))
                    {
                        setInvalidLink(shortUrl);
                        log.info("链接不在有效期范围内: {}", shortUrl);
                        return notFoundUri;
                    }
                    // 3.2.3 校验通过，写入缓存，同时返回
                    setShortLinkToCache(link);
                    log.info("成功跳转: {}", shortUrl);
                    shortLinkStatsSaveProducer.sendMessage(buildLinkStatsRecordAndSetUser(shortUrl, request, response));

                    return link.getOriginUrl();
                }, shortUrl);
    }


    @Override
    public void validLink(Link link, boolean add)
    {
        ThrowUtils.throwIf(link == null, ErrorCode.PARAMS_ERROR, "链接对象不能为空");

        // 校验原始链接
        String originUrl = link.getOriginUrl();
        ThrowUtils.throwIf(StringUtils.isBlank(originUrl) || !RegexUtils.validUrl(originUrl),
                ErrorCode.PARAMS_ERROR, "原始链接格式错误");

        // 校验描述
        ThrowUtils.throwIf(StringUtils.isBlank(link.getDescription()), ErrorCode.PARAMS_ERROR, "描述不能为空");

        // 校验分组 ID
        ThrowUtils.throwIf(StringUtils.isBlank(link.getGid()), ErrorCode.PARAMS_ERROR, "分组 ID 不能为空");

        // 校验有效期类型
        checkLinkValidDate(link);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LinkCreateVO addShortLinkFormWeb(LinkAddRequest linkAddRequest)
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
                        .validDateStart(linkAddRequest.getValidDateStart())
                        .validDateEnd(linkAddRequest.getValidDateEnd())
                        .createdType(ShortLinkCreateType.CONSOLE.getCode())
                        .enableStatus(CommonConstant.ENABLE_STATUS)
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
        try {
            baseMapper.insert(link);
            linkGotoMapper.insert(linkGoto);
        }
        catch (DuplicateKeyException exception)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "短链接已存在");
        }
        // 添加进布隆过滤器
        shortLinkFilter.add(shortLinkSuffix);
        setShortLinkToCache(link);
        return LinkCreateVO.builder()
                           .gid(linkAddRequest.getGid())
                           .shortLink(finalShortLink)
                           .originUrl(linkAddRequest.getOriginUrl())
                           .build();
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

    /**
     * 调整链接状态
     * todo 调整为责任链模式，不仅可以校验参数，还能直接更新更新数据库。
     *
     * @author CAIXYPROMISE
     */
    @Override
    public Boolean toggleLinkStatus(Long linkId, String groupId, UserVO loginUser, Integer status)
    {
        // 1. 检查status合法性
        if (status == null || status < 0 || status > 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Link link = getLinkByGroupAndUser(groupId, linkId, loginUser);
        // 4. 修改状态
        link.setEnableStatus(status);
        Boolean isSuccess = baseMapper.updateById(link) > 0;
        if (isSuccess) {
            // 5. 更新缓存
            setShortLinkToCache(link);
        }
        return isSuccess;
    }


    @Override
    public Boolean updateLinkValidDate(LinkUpdateValidDateRequest linkUpdateValidDateRequest, UserVO userVO) {
        // 1. 根据用户获取对应分组内对应id的链接信息
        Link linkByGroupAndUser = getLinkByGroupAndUser(linkUpdateValidDateRequest.getGroupId(),
                linkUpdateValidDateRequest.getLinkId(), userVO);
        // 先更新值
        linkByGroupAndUser.setValidDateType(linkUpdateValidDateRequest.getValidDateType());
        linkByGroupAndUser.setValidDateStart(linkUpdateValidDateRequest.getValidDateStart());
        linkByGroupAndUser.setValidDateEnd(linkUpdateValidDateRequest.getValidDateEnd());
        // 校验时间合法性
        checkLinkValidDate(linkByGroupAndUser);
        // 2. 更新数据库
        return baseMapper.updateById(linkByGroupAndUser) > 0;
    }

    /**
     * 更新分组
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/13 19:38
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean moveLinksToGroup(String groupId, String newGroupId, Collection<Long> linkIds, UserVO loginUser)
    {
        // 1. 检查新分组是否存在
        Group newGroup = groupMapper.findGroupByGid(newGroupId);
        if (newGroup == null || !newGroup.getUsername().equals(loginUser.getNickName())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新的分组不存在或没有操作权限");
        }
        // 1.1 检查原来的分组是否存在
        Group originGroup = groupMapper.findGroupByGid(groupId);
        if (originGroup == null || !originGroup.getUsername().equals(loginUser.getNickName())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "分组不存在或没有操作权限");
        }
        // 2. 获取链接信息
        LambdaQueryWrapper<Link> linkLambdaQueryWrapper = new LambdaQueryWrapper<>();
        linkLambdaQueryWrapper.eq(Link::getGid, groupId)
                              .in(Link::getId, linkIds)
                              .eq(Link::getIsDeleted, CommonConstant.NOT_DELETE_FLAG);
        List<Link> linkList = list(linkLambdaQueryWrapper);
        // 2.1 检查链接是否存在, 数量是否匹配
        // tips: 这里不需要校验链接是不是操作人的，因为前面已经校验过分组信息了，分组与用户强绑定, 后续如果有需要团队检查再做确认
        if (linkList.isEmpty() || linkList.size() != linkIds.size()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "链接不存在");
        }
        // 3. 修改分组
        Set<String> shortUrlList = new HashSet<>();
        linkList.forEach(link -> {
            link.setGid(newGroupId);
            shortUrlList.add(link.getFullShortUrl());
        });
        LambdaUpdateWrapper<LinkGoto> linkGotoUpdateWrapper = new LambdaUpdateWrapper<>();
        linkGotoUpdateWrapper.set(LinkGoto::getGid, newGroupId)
                              .in(LinkGoto::getFullShortUrl, shortUrlList);
        // 4. 更新数据库
        return this.updateBatchById(linkList) && linkGotoMapper.update(null, linkGotoUpdateWrapper) > 0;
    }

    private ShortLinkStatsRecordDTO buildLinkStatsRecordAndSetUser(String fullShortUrl, HttpServletRequest request, HttpServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Cookie[] cookies = request.getCookies();
        AtomicReference<String> uv = new AtomicReference<>();
        Runnable addResponseCookieTask = () -> {
            uv.set(UUID.randomUUID().toString());
            Cookie uvCookie = new Cookie("uv", uv.get());
            // 设置 cookie 的过期时间为 30 天（单位 秒）
            uvCookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(30));
            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
            response.addCookie(uvCookie);
            uvFirstFlag.set(Boolean.TRUE);
            redisManager.addToSet(RedisKeyEnum.LINK_STATS_UV_KEY, uv.get(), fullShortUrl);
        };
        if (ArrayUtil.isNotEmpty(cookies)) {
            Arrays.stream(cookies)
                  .filter(each -> Objects.equals(each.getName(), "uv"))
                  .findFirst()
                  .map(Cookie::getValue)
                  .ifPresentOrElse(each -> {
                      uv.set(each);
                      Long uvAdded = redisManager.addToSet(RedisKeyEnum.LINK_STATS_UIP_KEY, each, fullShortUrl);
                      uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                  }, addResponseCookieTask);
        } else {
            addResponseCookieTask.run();
        }
        String remoteAddr = NetUtils.getIpAddress(request);
        String os = NetUtils.getOs(request);
        String browser = NetUtils.getBrowser(request);
        String device = NetUtils.getDevice(request);
        String network = NetUtils.getNetwork(request);
        Long uipAdded = redisManager.addToSet(RedisKeyEnum.LINK_STATS_UIP_KEY, remoteAddr, fullShortUrl);
        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
        return ShortLinkStatsRecordDTO.builder()
                                      .fullShortUrl(fullShortUrl)
                                      .uv(uv.get())
                                      .uvFirstFlag(uvFirstFlag.get())
                                      .uipFirstFlag(uipFirstFlag)
                                      .remoteAddr(remoteAddr)
                                      .os(os)
                                      .browser(browser)
                                      .device(device)
                                      .network(network)
                                      .currentDate(new Date())
                                      .build();
    }

    /**
     * 根据用户获取对应分组内对应id的链接信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/29 3:06
     */
    private Link getLinkByGroupAndUser(String groupId, Long linkId, UserVO loginUser)
    {
        // 2. 先查询出链接的所在分组，并且检查权限
        Group group = groupService.findByGidAndCheckAccess(groupId, loginUser);
        // 3. 查询对应分组内的链接信息
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Link::getId, linkId)
                .eq(Link::getGid, group.getGid());
        Link link = baseMapper.selectOne(queryWrapper);
        if (link == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "链接不存在或者修改权限不足");
        }
        return link;
    }

    /**
     * 检查链接是否在无效链接内
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/29 3:04
     */
    private boolean isInvalidLink(String url)
    {
        String urlInCache = redisManager.getString(RedisKeyEnum.INVALID_SHORT_LINK, url);
        return StringUtils.isNotBlank(urlInCache);
    }

    /**
     * 设置无效链接
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/29 3:03
     */
    private void setInvalidLink(String url)
    {
        redisManager.setString(RedisKeyEnum.INVALID_SHORT_LINK, "1", url);
    }

    /**
     * 从缓存获取短链信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/28 18:37
     */
    private String getShortLinkFormCache(String shortUrl)
    {
        if (StringUtils.isNotBlank(shortUrl))
        {
            Link linkOnCache = redisManager.getObject(RedisKeyEnum.TARGET_SHORT_LINK, Link.class, shortUrl).orElse(null);
            if (linkOnCache != null)
            {
                if (Objects.equals(linkOnCache.getEnableStatus(), CommonConstant.ENABLE_STATUS)) {
                    return linkOnCache.getOriginUrl();
                }
            }
        }
        return null;
    }

    /**
     * 设置短链信息进入缓存内
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2024/11/28 18:37
     */
    private void setShortLinkToCache(Link link)
    {
        if (link.getValidDateType().equals(ShortLinkDateType.PERMANENT.getCode())) {
            // 永久有效，直接存储到 Redis
            redisManager.setObject(RedisKeyEnum.TARGET_SHORT_LINK, link, link.getFullShortUrl());
            log.info("Link [{}] cached in Redis with an expiration time of [{}]", link.getFullShortUrl(), "Permanent");
            return;
        }
        // 计算过期时间（毫秒转秒）
        long expireTimeMillis = link.getValidDateEnd().getTime() - System.currentTimeMillis();
        // 链接已过期，不存入 Redis
        if (expireTimeMillis <= 0) {
            log.info("Link [{}] has already expired, skipping caching", link.getFullShortUrl());
            return;
        }
        long expireTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(expireTimeMillis);
        redisManager.setObject(RedisKeyEnum.TARGET_SHORT_LINK, link, expireTimeSeconds, link.getFullShortUrl());
        log.info("Link [{}] cached in Redis with an expiration time of [{}] seconds", link.getFullShortUrl(), expireTimeSeconds);
    }

    private void checkLinkValidDate(Link link)
    {
        ShortLinkDateType dateType = ShortLinkDateType.getEnumByCode(link.getValidDateType());
        ThrowUtils.throwIf(dateType == null, ErrorCode.PARAMS_ERROR, "有效日期类型错误");

        // 如果是自定义有效期，校验开始时间和结束时间
        if (dateType.equals(ShortLinkDateType.CUSTOM))
        {
            Date validDateStart = link.getValidDateStart();
            Date validDateEnd = link.getValidDateEnd();

            ThrowUtils.throwIf(validDateStart == null || validDateEnd == null,
                    ErrorCode.PARAMS_ERROR, "自定义有效期的开始时间和结束时间不能为空");

            ThrowUtils.throwIf(DateUtils.isBeforeNow(validDateStart),
                    ErrorCode.PARAMS_ERROR, "有效期的开始时间不能早于当前时间");
            ThrowUtils.throwIf(!DateUtils.isAfter(validDateEnd, validDateStart),
                    ErrorCode.PARAMS_ERROR, "有效期的结束时间必须晚于开始时间");
        }
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
        // 如果布隆过滤器内发现不存在，去数据库确认一遍
        if (StringUtils.isNotBlank(suffix) && !shortLinkFilter.contains(suffix))
        {
            return this.baseMapper.findShortLinkBySuffix(suffix) != null;
        }
        return false;
    }

}
