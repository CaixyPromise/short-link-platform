package com.caixy.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.mapper.*;
import com.caixy.shortlink.model.convertor.linkAccessStats.LinkAccessStatsConvertor;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkGroupStatsAccessRecordReqDTO;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkStatsAccessRecordReqDTO;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkStatsRecordDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkGroupStatsReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkStatsReqDTO;
import com.caixy.shortlink.model.dto.linkLocaleStats.LinkLocaleStateIpApiResponse;
import com.caixy.shortlink.model.entity.*;

import com.caixy.shortlink.model.enums.RedisReadWriteKeyEnum;
import com.caixy.shortlink.model.vo.linkAccessStats.*;

import com.caixy.shortlink.service.GroupService;
import com.caixy.shortlink.service.LinkAccessStatsService;
import com.caixy.shortlink.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 短链接访问统计服务实现
 *
 * @author: CAIXYPROMISE
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LinkAccessStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStats> implements LinkAccessStatsService
{
    private static final LinkAccessStatsConvertor linkAccessStatsConvertor = LinkAccessStatsConvertor.INSTANCE;
    private final GroupService groupService;
    private final LinkMapper shortLinkMapper;
    private final LinkGotoMapper shortLinkGotoMapper;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveShortLinkStats(ShortLinkStatsRecordDTO statsRecord)
    {
        String fullShortUrl = statsRecord.getFullShortUrl();
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(
                RedisReadWriteKeyEnum.LINK_STAT.generateKey(fullShortUrl));
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try
        {
            LambdaQueryWrapper<LinkGoto> queryWrapper = Wrappers.lambdaQuery(LinkGoto.class)
                                                                .eq(LinkGoto::getFullShortUrl, fullShortUrl);
            LinkGoto shortLinkGoto = shortLinkGotoMapper.selectOne(queryWrapper);
            String gid = shortLinkGoto.getGid();
            Date currentDate = statsRecord.getCurrentDate();
            int hour = DateUtil.hour(currentDate, true);
            Week week = DateUtil.dayOfWeekEnum(currentDate);
            int weekValue = week.getIso8601Value();
            LinkAccessStats linkAccessStats = LinkAccessStats.builder()
                                                             .pv(1)
                                                             .uv(statsRecord.getUvFirstFlag() ? 1 : 0)
                                                             .uip(statsRecord.getUipFirstFlag() ? 1 : 0)
                                                             .hour(hour)
                                                             .weekday(weekValue)
                                                             .fullShortUrl(fullShortUrl)
                                                             .date(currentDate)
                                                             .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStats);
            LinkLocaleStateIpApiResponse localeResultObj = LinkLocaleStateIpApiResponse
                    .fetchLocaleInfo(statsRecord.getRemoteAddr());
            log.info("地区访问监控数据: {}", localeResultObj);
            if (localeResultObj.getIsSucceed())
            {
                String province = localeResultObj.getProvince();
                boolean unknownFlag = StrUtil.equals(province, "[]");
                LinkLocaleStats linkLocaleStats = LinkLocaleStats.builder()
                                                                 .province(localeResultObj.getProvince())
                                                                 .city(localeResultObj.getCity())
                                                                 .adcode(unknownFlag ? "未知"
                                                                                     : localeResultObj.getAdcode())
                                                                 .cnt(1)
                                                                 .fullShortUrl(fullShortUrl)
                                                                 .country(localeResultObj.getCountry())
                                                                 .date(currentDate)
                                                                 .build();
                linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStats);
            }
            LinkOsStats linkOsStats = LinkOsStats.builder()
                                                 .os(statsRecord.getOs())
                                                 .cnt(1)
                                                 .fullShortUrl(fullShortUrl)
                                                 .date(currentDate)
                                                 .build();
            linkOsStatsMapper.shortLinkOsState(linkOsStats);
            LinkBrowserStats linkBrowserStats = LinkBrowserStats.builder()
                                                                .browser(statsRecord.getBrowser())
                                                                .cnt(1)
                                                                .fullShortUrl(fullShortUrl)
                                                                .date(currentDate)
                                                                .build();
            linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStats);
            LinkDeviceStats linkDeviceStats = LinkDeviceStats.builder()
                                                             .device(statsRecord.getDevice())
                                                             .cnt(1)
                                                             .fullShortUrl(fullShortUrl)
                                                             .date(currentDate)
                                                             .build();
            linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStats);
            LinkNetworkStats linkNetworkStats = LinkNetworkStats.builder()
                                                                .network(statsRecord.getNetwork())
                                                                .cnt(1)
                                                                .fullShortUrl(fullShortUrl)
                                                                .date(currentDate)
                                                                .build();
            linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStats);
            LinkAccessLogs linkAccessLogs = LinkAccessLogs.builder()
                                                          .user(statsRecord.getUv())
                                                          .ip(statsRecord.getRemoteAddr())
                                                          .browser(statsRecord.getBrowser())
                                                          .os(statsRecord.getOs())
                                                          .network(statsRecord.getNetwork())
                                                          .device(statsRecord.getDevice())
                                                          .locale(localeResultObj.getLocation())
                                                          .fullShortUrl(fullShortUrl)
                                                          .build();
            linkAccessLogsMapper.insert(linkAccessLogs);
            shortLinkMapper.incrementStats(gid, fullShortUrl, 1, statsRecord.getUvFirstFlag() ? 1 : 0,
                    statsRecord.getUipFirstFlag() ? 1 : 0);
            LinkStatsToday linkStatsToday = LinkStatsToday.builder()
                                                          .todayPv(1)
                                                          .todayUv(statsRecord.getUvFirstFlag() ? 1 : 0)
                                                          .todayUip(statsRecord.getUipFirstFlag() ? 1 : 0)
                                                          .fullShortUrl(fullShortUrl)
                                                          .date(currentDate)
                                                          .build();
            linkStatsTodayMapper.shortLinkTodayState(linkStatsToday);
        }
        finally
        {
            rLock.unlock();
        }
    }

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam)
    {
        groupService.checkGroupBelongToUser(requestParam.getGid());
        List<LinkAccessStats> listStatsByShortLink = linkAccessStatsMapper.listStatsByShortLink(requestParam);
        if (CollUtil.isEmpty(listStatsByShortLink))
        {
            return null;
        }
        // 基础访问数据
        LinkAccessStats pvUvUidStatsByShortLink = linkAccessLogsMapper.findPvUvUidStatsByShortLink(requestParam);
        // 基础访问详情
        List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
        // 解析字符串为 Date 对象
        List<String> rangeDates = DateUtil.rangeToList(
                                                  DateUtil.parse(requestParam.getStartDate(), "yyyy-MM-dd"),
                                                  DateUtil.parse(requestParam.getEndDate(), "yyyy-MM-dd"),
                                                  DateField.DAY_OF_MONTH
                                          ).stream()
                                          .map(DateUtil::formatDate)
                                          .toList();

        rangeDates.forEach(each -> listStatsByShortLink
                .stream()
                .filter(item -> Objects.equals(each,
                        DateUtil.formatDate(item.getDate())))
                .findFirst()
                .ifPresentOrElse(item ->
                {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO =
                            ShortLinkStatsAccessDailyRespDTO.builder()
                                                            .date(each)
                                                            .pv(item.getPv())
                                                            .uv(item.getUv())
                                                            .uip(item.getUip())
                                                            .build();
                    daily.add(accessDailyRespDTO);
                }, () ->
                {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO =
                            ShortLinkStatsAccessDailyRespDTO.builder()
                                                            .date(each)
                                                            .pv(0)
                                                            .uv(0)
                                                            .uip(0)
                                                            .build();
                    daily.add(accessDailyRespDTO);
                }));
        // 地区访问详情（仅国内）
        List<ShortLinkStatsLocaleCNRespDTO> localeCnStats = new ArrayList<>();
        List<LinkLocaleStats> listedLocaleByShortLink = linkLocaleStatsMapper.listLocaleByShortLink(requestParam);
        int localeCnSum = listedLocaleByShortLink.stream()
                                                 .mapToInt(LinkLocaleStats::getCnt)
                                                 .sum();
        listedLocaleByShortLink.forEach(each ->
        {
            double ratio = (double) each.getCnt() / localeCnSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsLocaleCNRespDTO localeCNRespDTO =
                    ShortLinkStatsLocaleCNRespDTO.builder()
                                                 .cnt(each.getCnt())
                                                 .locale(each.getProvince())
                                                 .ratio(actualRatio)
                                                 .build();
            localeCnStats.add(localeCNRespDTO);
        });
        // 小时访问详情
        List<Integer> hourStats = new ArrayList<>();
        List<LinkAccessStats> listHourStatsByShortLink = linkAccessStatsMapper.listHourStatsByShortLink(requestParam);
        for (int i = 0; i < 24; i++)
        {
            AtomicInteger hour = new AtomicInteger(i);
            int hourCnt = listHourStatsByShortLink.stream()
                                                  .filter(each -> Objects.equals(each.getHour(), hour.get()))
                                                  .findFirst()
                                                  .map(LinkAccessStats::getPv)
                                                  .orElse(0);
            hourStats.add(hourCnt);
        }
        // 高频访问IP详情
        List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
        List<HashMap<String, Object>> listTopIpByShortLink = linkAccessLogsMapper.listTopIpByShortLink(requestParam);
        listTopIpByShortLink.forEach(each ->
        {
            ShortLinkStatsTopIpRespDTO statsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                                                                                     .ip(each.get("ip").toString())
                                                                                     .cnt(Integer.parseInt(each.get(
                                                                                             "count").toString()))
                                                                                     .build();
            topIpStats.add(statsTopIpRespDTO);
        });
        // 一周访问详情
        List<Integer> weekdayStats = new ArrayList<>();
        List<LinkAccessStats> listWeekdayStatsByShortLink = linkAccessStatsMapper.listWeekdayStatsByShortLink(
                requestParam);
        for (int i = 1; i < 8; i++)
        {
            AtomicInteger weekday = new AtomicInteger(i);
            int weekdayCnt = listWeekdayStatsByShortLink.stream()
                                                        .filter(each -> Objects.equals(each.getWeekday(),
                                                                weekday.get()))
                                                        .findFirst()
                                                        .map(LinkAccessStats::getPv)
                                                        .orElse(0);
            weekdayStats.add(weekdayCnt);
        }
        // 浏览器访问详情
        List<ShortLinkStatsBrowserRespDTO> browserStats = new ArrayList<>();
        List<HashMap<String, Object>> listBrowserStatsByShortLink = linkBrowserStatsMapper.listBrowserStatsByShortLink(
                requestParam);
        int browserSum = listBrowserStatsByShortLink.stream()
                                                    .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                                                    .sum();
        listBrowserStatsByShortLink.forEach(each ->
        {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / browserSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                                                                                      .cnt(Integer.parseInt(each.get(
                                                                                              "count").toString()))
                                                                                      .browser(each.get(
                                                                                              "browser").toString())
                                                                                      .ratio(actualRatio)
                                                                                      .build();
            browserStats.add(browserRespDTO);
        });
        // 操作系统访问详情
        List<ShortLinkStatsOsRespDTO> osStats = new ArrayList<>();
        List<HashMap<String, Object>> listOsStatsByShortLink = linkOsStatsMapper.listOsStatsByShortLink(requestParam);
        int osSum = listOsStatsByShortLink.stream()
                                          .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                                          .sum();
        listOsStatsByShortLink.forEach(each ->
        {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / osSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
                                                                       .cnt(Integer.parseInt(
                                                                               each.get("count").toString()))
                                                                       .os(each.get("os").toString())
                                                                       .ratio(actualRatio)
                                                                       .build();
            osStats.add(osRespDTO);
        });
        // 访客访问类型详情
        List<ShortLinkStatsUvRespDTO> uvTypeStats = new ArrayList<>();
        HashMap<String, Object> findUvTypeByShortLink = linkAccessLogsMapper.findUvTypeCntByShortLink(requestParam);
        int oldUserCnt = Integer.parseInt(
                Optional.ofNullable(findUvTypeByShortLink)
                        .map(each -> each.get("oldUserCnt"))
                        .map(Object::toString)
                        .orElse("0")
        );
        int newUserCnt = Integer.parseInt(
                Optional.ofNullable(findUvTypeByShortLink)
                        .map(each -> each.get("newUserCnt"))
                        .map(Object::toString)
                        .orElse("0")
        );
        int uvSum = oldUserCnt + newUserCnt;
        double oldRatio = (double) oldUserCnt / uvSum;
        double actualOldRatio = Math.round(oldRatio * 100.0) / 100.0;
        double newRatio = (double) newUserCnt / uvSum;
        double actualNewRatio = Math.round(newRatio * 100.0) / 100.0;
        ShortLinkStatsUvRespDTO newUvRespDTO = ShortLinkStatsUvRespDTO.builder()
                                                                      .uvType("newUser")
                                                                      .cnt(newUserCnt)
                                                                      .ratio(actualNewRatio)
                                                                      .build();
        uvTypeStats.add(newUvRespDTO);
        ShortLinkStatsUvRespDTO oldUvRespDTO = ShortLinkStatsUvRespDTO.builder()
                                                                      .uvType("oldUser")
                                                                      .cnt(oldUserCnt)
                                                                      .ratio(actualOldRatio)
                                                                      .build();
        uvTypeStats.add(oldUvRespDTO);
        // 访问设备类型详情
        List<ShortLinkStatsDeviceRespDTO> deviceStats = new ArrayList<>();
        List<LinkDeviceStats> listDeviceStatsByShortLink = linkDeviceStatsMapper.listDeviceStatsByShortLink(
                requestParam);
        int deviceSum = listDeviceStatsByShortLink.stream()
                                                  .mapToInt(LinkDeviceStats::getCnt)
                                                  .sum();
        listDeviceStatsByShortLink.forEach(each ->
        {
            double ratio = (double) each.getCnt() / deviceSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                                                                                   .cnt(each.getCnt())
                                                                                   .device(each.getDevice())
                                                                                   .ratio(actualRatio)
                                                                                   .build();
            deviceStats.add(deviceRespDTO);
        });
        // 访问网络类型详情
        List<ShortLinkStatsNetworkRespDTO> networkStats = new ArrayList<>();
        List<LinkNetworkStats> listNetworkStatsByShortLink = linkNetworkStatsMapper.listNetworkStatsByShortLink(
                requestParam);
        int networkSum = listNetworkStatsByShortLink.stream()
                                                    .mapToInt(LinkNetworkStats::getCnt)
                                                    .sum();
        listNetworkStatsByShortLink.forEach(each ->
        {
            double ratio = (double) each.getCnt() / networkSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                                                                                      .cnt(each.getCnt())
                                                                                      .network(each.getNetwork())
                                                                                      .ratio(actualRatio)
                                                                                      .build();
            networkStats.add(networkRespDTO);
        });
        ShortLinkStatsRespDTO shortLinkStatsRespDTO = ShortLinkStatsRespDTO.builder()
                                                           .pv(pvUvUidStatsByShortLink.getPv())
                                                           .uv(pvUvUidStatsByShortLink.getUv())
                                                           .uip(pvUvUidStatsByShortLink.getUip())
                                                           .daily(daily)
                                                           .localeCnStats(localeCnStats)
                                                           .hourStats(hourStats)
                                                           .topIpStats(topIpStats)
                                                           .weekdayStats(weekdayStats)
                                                           .browserStats(browserStats)
                                                           .osStats(osStats)
                                                           .uvTypeStats(uvTypeStats)
                                                           .deviceStats(deviceStats)
                                                           .networkStats(networkStats)
                                                           .build();
        log.info("shortLinkStatsRespDTO: {}", shortLinkStatsRespDTO);
        return shortLinkStatsRespDTO;
    }

    @Override
    public ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam)
    {
        groupService.checkGroupBelongToUser(requestParam.getGid());
        List<LinkAccessStats> listStatsByGroup = linkAccessStatsMapper.listStatsByGroup(requestParam);
        if (CollUtil.isEmpty(listStatsByGroup))
        {
            return null;
        }
        // 基础访问数据
        LinkAccessStats pvUvUidStatsByGroup = linkAccessLogsMapper.findPvUvUidStatsByGroup(requestParam);
        // 基础访问详情
        List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
        List<String> rangeDates = DateUtil.rangeToList(DateUtil.parse(requestParam.getStartDate()),
                                                  DateUtil.parse(requestParam.getEndDate()), DateField.DAY_OF_MONTH).stream()
                                          .map(DateUtil::formatDate)
                                          .toList();
        rangeDates.forEach(each -> listStatsByGroup
                .stream()
                .filter(item -> Objects.equals(each,
                        DateUtil.formatDate(item.getDate())))
                .findFirst()
                .ifPresentOrElse(item ->
                {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO =
                            ShortLinkStatsAccessDailyRespDTO.builder()
                                                            .date(each)
                                                            .pv(item.getPv())
                                                            .uv(item.getUv())
                                                            .uip(item.getUip())
                                                            .build();
                    daily.add(accessDailyRespDTO);
                }, () ->
                {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                                                                                                          .date(each)
                                                                                                          .pv(0)
                                                                                                          .uv(0)
                                                                                                          .uip(0)
                                                                                                          .build();
                    daily.add(accessDailyRespDTO);
                }));
        // 地区访问详情（仅国内）
        List<ShortLinkStatsLocaleCNRespDTO> localeCnStats = new ArrayList<>();
        List<LinkLocaleStats> listedLocaleByGroup = linkLocaleStatsMapper.listLocaleByGroup(requestParam);
        int localeCnSum = listedLocaleByGroup.stream()
                                             .mapToInt(LinkLocaleStats::getCnt)
                                             .sum();
        listedLocaleByGroup.forEach(each ->
        {
            double ratio = (double) each.getCnt() / localeCnSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsLocaleCNRespDTO localeCNRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
                                                                                         .cnt(each.getCnt())
                                                                                         .locale(each.getProvince())
                                                                                         .ratio(actualRatio)
                                                                                         .build();
            localeCnStats.add(localeCNRespDTO);
        });
        // 小时访问详情
        List<Integer> hourStats = new ArrayList<>();
        List<LinkAccessStats> listHourStatsByGroup = linkAccessStatsMapper.listHourStatsByGroup(requestParam);
        for (int i = 0; i < 24; i++)
        {
            AtomicInteger hour = new AtomicInteger(i);
            int hourCnt = listHourStatsByGroup.stream()
                                              .filter(each -> Objects.equals(each.getHour(), hour.get()))
                                              .findFirst()
                                              .map(LinkAccessStats::getPv)
                                              .orElse(0);
            hourStats.add(hourCnt);
        }
        // 高频访问IP详情
        List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
        List<HashMap<String, Object>> listTopIpByGroup = linkAccessLogsMapper.listTopIpByGroup(requestParam);
        listTopIpByGroup.forEach(each ->
        {
            ShortLinkStatsTopIpRespDTO statsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                                                                                     .ip(each.get("ip").toString())
                                                                                     .cnt(Integer.parseInt(each.get(
                                                                                             "count").toString()))
                                                                                     .build();
            topIpStats.add(statsTopIpRespDTO);
        });
        // 一周访问详情
        List<Integer> weekdayStats = new ArrayList<>();
        List<LinkAccessStats> listWeekdayStatsByGroup = linkAccessStatsMapper.listWeekdayStatsByGroup(requestParam);
        for (int i = 1; i < 8; i++)
        {
            AtomicInteger weekday = new AtomicInteger(i);
            int weekdayCnt = listWeekdayStatsByGroup.stream()
                                                    .filter(each -> Objects.equals(each.getWeekday(), weekday.get()))
                                                    .findFirst()
                                                    .map(LinkAccessStats::getPv)
                                                    .orElse(0);
            weekdayStats.add(weekdayCnt);
        }
        // 浏览器访问详情
        List<ShortLinkStatsBrowserRespDTO> browserStats = new ArrayList<>();
        List<HashMap<String, Object>> listBrowserStatsByGroup = linkBrowserStatsMapper.listBrowserStatsByGroup(
                requestParam);
        int browserSum = listBrowserStatsByGroup.stream()
                                                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                                                .sum();
        listBrowserStatsByGroup.forEach(each ->
        {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / browserSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                                                                                      .cnt(Integer.parseInt(each.get(
                                                                                              "count").toString()))
                                                                                      .browser(each.get(
                                                                                              "browser").toString())
                                                                                      .ratio(actualRatio)
                                                                                      .build();
            browserStats.add(browserRespDTO);
        });
        // 操作系统访问详情
        List<ShortLinkStatsOsRespDTO> osStats = new ArrayList<>();
        List<HashMap<String, Object>> listOsStatsByGroup = linkOsStatsMapper.listOsStatsByGroup(requestParam);
        int osSum = listOsStatsByGroup.stream()
                                      .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                                      .sum();
        listOsStatsByGroup.forEach(each ->
        {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / osSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
                                                                       .cnt(Integer.parseInt(
                                                                               each.get("count").toString()))
                                                                       .os(each.get("os").toString())
                                                                       .ratio(actualRatio)
                                                                       .build();
            osStats.add(osRespDTO);
        });
        // 访问设备类型详情
        List<ShortLinkStatsDeviceRespDTO> deviceStats = new ArrayList<>();
        List<LinkDeviceStats> listDeviceStatsByGroup = linkDeviceStatsMapper.listDeviceStatsByGroup(requestParam);
        int deviceSum = listDeviceStatsByGroup.stream()
                                              .mapToInt(LinkDeviceStats::getCnt)
                                              .sum();
        listDeviceStatsByGroup.forEach(each ->
        {
            double ratio = (double) each.getCnt() / deviceSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                                                                                   .cnt(each.getCnt())
                                                                                   .device(each.getDevice())
                                                                                   .ratio(actualRatio)
                                                                                   .build();
            deviceStats.add(deviceRespDTO);
        });
        // 访问网络类型详情
        List<ShortLinkStatsNetworkRespDTO> networkStats = new ArrayList<>();
        List<LinkNetworkStats> listNetworkStatsByGroup = linkNetworkStatsMapper.listNetworkStatsByGroup(requestParam);
        int networkSum = listNetworkStatsByGroup.stream()
                                                .mapToInt(LinkNetworkStats::getCnt)
                                                .sum();
        listNetworkStatsByGroup.forEach(each ->
        {
            double ratio = (double) each.getCnt() / networkSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                                                                                      .cnt(each.getCnt())
                                                                                      .network(each.getNetwork())
                                                                                      .ratio(actualRatio)
                                                                                      .build();
            networkStats.add(networkRespDTO);
        });
        return ShortLinkStatsRespDTO.builder()
                                    .pv(pvUvUidStatsByGroup.getPv())
                                    .uv(pvUvUidStatsByGroup.getUv())
                                    .uip(pvUvUidStatsByGroup.getUip())
                                    .daily(daily)
                                    .localeCnStats(localeCnStats)
                                    .hourStats(hourStats)
                                    .topIpStats(topIpStats)
                                    .weekdayStats(weekdayStats)
                                    .browserStats(browserStats)
                                    .osStats(osStats)
                                    .deviceStats(deviceStats)
                                    .networkStats(networkStats)
                                    .build();
    }

    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(
            ShortLinkStatsAccessRecordReqDTO requestParam)
    {
        groupService.checkGroupBelongToUser(requestParam.getGid());
        LambdaQueryWrapper<LinkAccessLogs> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LinkAccessLogs::getFullShortUrl, requestParam.getFullShortUrl())
                    .between(LinkAccessLogs::getCreateTime, requestParam.getStartDate(), requestParam.getEndDate())
                    .eq(LinkAccessLogs::getIsDeleted, CommonConstant.NOT_DELETE_FLAG)
                    .orderByDesc(LinkAccessLogs::getCreateTime);
        IPage<LinkAccessLogs> linkAccessLogsIPage = linkAccessLogsMapper.selectPage(requestParam, queryWrapper);
        if (CollUtil.isEmpty(linkAccessLogsIPage.getRecords()))
        {
            return new Page<>();
        }
        IPage<ShortLinkStatsAccessRecordRespDTO> actualResult = linkAccessLogsIPage.convert(
                each -> BeanUtil.toBean(each, ShortLinkStatsAccessRecordRespDTO.class));
        List<String> userAccessLogsList = actualResult.getRecords().stream()
                                                      .map(ShortLinkStatsAccessRecordRespDTO::getUser)
                                                      .toList();
        List<Map<String, Object>> uvTypeList = linkAccessLogsMapper.selectUvTypeByUsers(
                requestParam.getGid(),
                requestParam.getFullShortUrl(),
                requestParam.getEnableStatus(),
                requestParam.getStartDate(),
                requestParam.getEndDate(),
                userAccessLogsList
        );
        return getShortLinkStatsAccessRecordRespDTOIPage(actualResult, uvTypeList);
    }

    private IPage<ShortLinkStatsAccessRecordRespDTO> getShortLinkStatsAccessRecordRespDTOIPage(
            IPage<ShortLinkStatsAccessRecordRespDTO> actualResult, List<Map<String, Object>> uvTypeList)
    {
        actualResult.getRecords().forEach(each -> {
            String uvType = uvTypeList.stream()
                                      .filter(item -> Objects.equals(each.getUser(), item.get("user")))
                                      .findFirst()
                                      .map(item -> item.get("uvType"))
                                      .map(Object::toString)
                                      .orElse("旧访客");
            each.setUvType(uvType);
        });
        return actualResult;
    }

    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(
            ShortLinkGroupStatsAccessRecordReqDTO requestParam)
    {
        groupService.checkGroupBelongToUser(requestParam.getGid());
        IPage<LinkAccessLogs> linkAccessLogsIPage = linkAccessLogsMapper.selectGroupPage(requestParam);
        if (CollUtil.isEmpty(linkAccessLogsIPage.getRecords()))
        {
            return new Page<>();
        }
        IPage<ShortLinkStatsAccessRecordRespDTO> actualResult = linkAccessLogsIPage
                .convert(linkAccessStatsConvertor::AccessLogToRecordDto);
        List<String> userAccessLogsList = actualResult.getRecords().stream()
                                                      .map(ShortLinkStatsAccessRecordRespDTO::getUser)
                                                      .toList();
        List<Map<String, Object>> uvTypeList = linkAccessLogsMapper.selectGroupUvTypeByUsers(
                requestParam.getGid(),
                requestParam.getStartDate(),
                requestParam.getEndDate(),
                userAccessLogsList
        );
        return getShortLinkStatsAccessRecordRespDTOIPage(actualResult, uvTypeList);
    }


}
