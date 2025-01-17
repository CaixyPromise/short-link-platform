package com.caixy.shortlink.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.mapper.LinkLocaleStatsMapper;
import com.caixy.shortlink.model.dto.linkLocaleStats.LinkLocaleStatsQueryRequest;
import com.caixy.shortlink.model.entity.LinkLocaleStats;

import com.caixy.shortlink.model.entity.User;
import com.caixy.shortlink.model.vo.linkLocaleStats.LinkLocaleStatsVO;

import com.caixy.shortlink.service.LinkLocaleStatsService;
import com.caixy.shortlink.service.UserService;
import com.caixy.shortlink.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 短链接地域统计服务实现
 * @author: CAIXYPROMISE
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class LinkLocaleStatsServiceImpl extends ServiceImpl<LinkLocaleStatsMapper, LinkLocaleStats> implements LinkLocaleStatsService {

    private final UserService userService;

    /**
     * 校验数据
     *
     * @param linkLocaleStats
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validLinkLocaleStats(LinkLocaleStats linkLocaleStats, boolean add) {
        ThrowUtils.throwIf(linkLocaleStats == null, ErrorCode.PARAMS_ERROR);

        // 修改数据时，有参数则校验
        // todo 补充校验规则
    }

    /**
     * 获取查询条件
     *
     * @param linkLocaleStatsQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<LinkLocaleStats> getQueryWrapper(LinkLocaleStatsQueryRequest linkLocaleStatsQueryRequest) {
        QueryWrapper<LinkLocaleStats> queryWrapper = new QueryWrapper<>();
        if (linkLocaleStatsQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = linkLocaleStatsQueryRequest.getId();
        Long notId = linkLocaleStatsQueryRequest.getNotId();
        String title = linkLocaleStatsQueryRequest.getTitle();
        String content = linkLocaleStatsQueryRequest.getContent();
        String searchText = linkLocaleStatsQueryRequest.getSearchText();
        String sortField = linkLocaleStatsQueryRequest.getSortField();
        String sortOrder = linkLocaleStatsQueryRequest.getSortOrder();
        List<String> tagList = linkLocaleStatsQueryRequest.getTags();
        Long userId = linkLocaleStatsQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取短链接地域统计封装
     *
     * @param linkLocaleStats
     * @param request
     * @return
     */
    @Override
    public LinkLocaleStatsVO getLinkLocaleStatsVO(LinkLocaleStats linkLocaleStats, HttpServletRequest request) {
    // todo: 补充获取短链接地域统计封装逻辑
        return null;
    }

    /**
     * 分页获取短链接地域统计封装
     *
     * @param linkLocaleStatsPage
     * @param request
     * @return
     */
    @Override
    public Page<LinkLocaleStatsVO> getLinkLocaleStatsVOPage(Page<LinkLocaleStats> linkLocaleStatsPage, HttpServletRequest request) {
        // todo: 补充分页获取短链接地域统计封装逻辑
        return null;
    }

}
