package com.caixy.shortlink.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.mapper.LinkAccessLogsMapper;
import com.caixy.shortlink.model.dto.linkAccessLogs.LinkAccessLogsQueryRequest;
import com.caixy.shortlink.model.entity.LinkAccessLogs;

import com.caixy.shortlink.model.entity.User;
import com.caixy.shortlink.model.vo.linkAccessLogs.LinkAccessLogsVO;

import com.caixy.shortlink.service.LinkAccessLogsService;
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
 * 短链接访问日志服务实现
 * @author: CAIXYPROMISE
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class LinkAccessLogsServiceImpl extends ServiceImpl<LinkAccessLogsMapper, LinkAccessLogs> implements LinkAccessLogsService {

    private final UserService userService;

    /**
     * 校验数据
     *
     * @param linkAccessLogs
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validLinkAccessLogs(LinkAccessLogs linkAccessLogs, boolean add) {
        ThrowUtils.throwIf(linkAccessLogs == null, ErrorCode.PARAMS_ERROR);

        // 修改数据时，有参数则校验
        // todo 补充校验规则
    }

    /**
     * 获取查询条件
     *
     * @param linkAccessLogsQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<LinkAccessLogs> getQueryWrapper(LinkAccessLogsQueryRequest linkAccessLogsQueryRequest) {
        QueryWrapper<LinkAccessLogs> queryWrapper = new QueryWrapper<>();
        if (linkAccessLogsQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = linkAccessLogsQueryRequest.getId();
        Long notId = linkAccessLogsQueryRequest.getNotId();
        String title = linkAccessLogsQueryRequest.getTitle();
        String content = linkAccessLogsQueryRequest.getContent();
        String searchText = linkAccessLogsQueryRequest.getSearchText();
        String sortField = linkAccessLogsQueryRequest.getSortField();
        String sortOrder = linkAccessLogsQueryRequest.getSortOrder();
        List<String> tagList = linkAccessLogsQueryRequest.getTags();
        Long userId = linkAccessLogsQueryRequest.getUserId();
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
     * 获取短链接访问日志封装
     *
     * @param linkAccessLogs
     * @param request
     * @return
     */
    @Override
    public LinkAccessLogsVO getLinkAccessLogsVO(LinkAccessLogs linkAccessLogs, HttpServletRequest request) {
    // todo: 补充获取短链接访问日志封装逻辑
        return null;
    }

    /**
     * 分页获取短链接访问日志封装
     *
     * @param linkAccessLogsPage
     * @param request
     * @return
     */
    @Override
    public Page<LinkAccessLogsVO> getLinkAccessLogsVOPage(Page<LinkAccessLogs> linkAccessLogsPage, HttpServletRequest request) {
        // todo: 补充分页获取短链接访问日志封装逻辑
        return null;
    }

}
