package com.caixy.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.shortlink.annotation.AuthCheck;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.DeleteRequest;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.constant.UserConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkGroupStatsAccessRecordReqDTO;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkStatsAccessRecordReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.*;
import com.caixy.shortlink.model.entity.LinkAccessStats;
import com.caixy.shortlink.model.enums.UserRoleEnum;
import com.caixy.shortlink.model.vo.linkAccessStats.ShortLinkStatsAccessRecordRespDTO;
import com.caixy.shortlink.model.vo.linkAccessStats.ShortLinkStatsRespDTO;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.model.vo.linkAccessStats.LinkAccessStatsVO;
import com.caixy.shortlink.service.LinkAccessStatsService;
import com.caixy.shortlink.manager.Authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 短链接访问统计接口
 *
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/access-stats")
public class LinkAccessStatsController
{
    private final LinkAccessStatsService shortLinkStatsService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/one")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(@ModelAttribute ShortLinkStatsReqDTO requestParam)
    {
        if (requestParam == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(shortLinkStatsService.oneShortLinkStats(requestParam));
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(@ModelAttribute ShortLinkGroupStatsReqDTO requestParam)
    {
        return ResultUtils.success(shortLinkStatsService.groupShortLinkStats(requestParam));
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/access-logs")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(
           @ModelAttribute ShortLinkStatsAccessRecordReqDTO requestParam)
    {
        return ResultUtils.success(shortLinkStatsService.shortLinkStatsAccessRecord(requestParam));
    }

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    @GetMapping("/access-logs/group")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(
            @ModelAttribute ShortLinkGroupStatsAccessRecordReqDTO requestParam)
    {
        return ResultUtils.success(shortLinkStatsService.groupShortLinkStatsAccessRecord(requestParam));
    }
}
