package com.caixy.shortlink.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.ThrowUtils;
import com.caixy.shortlink.mapper.LinkGotoMapper;
import com.caixy.shortlink.model.dto.linkGoto.LinkGotoQueryRequest;
import com.caixy.shortlink.model.entity.LinkGoto;

import com.caixy.shortlink.model.entity.User;
import com.caixy.shortlink.model.vo.linkGoto.LinkGotoVO;

import com.caixy.shortlink.service.LinkGotoService;
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
 * 短链接跳转信息服务实现
 * @author: CAIXYPROMISE
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class LinkGotoServiceImpl extends ServiceImpl<LinkGotoMapper, LinkGoto> implements LinkGotoService {

    private final UserService userService;

    /**
     * 校验数据
     *
     * @param linkGoto
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validLinkGoto(LinkGoto linkGoto, boolean add) {
        ThrowUtils.throwIf(linkGoto == null, ErrorCode.PARAMS_ERROR);

        // 修改数据时，有参数则校验
        // todo 补充校验规则
    }

    /**
     * 获取查询条件
     *
     * @param linkGotoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<LinkGoto> getQueryWrapper(LinkGotoQueryRequest linkGotoQueryRequest) {
        QueryWrapper<LinkGoto> queryWrapper = new QueryWrapper<>();
        if (linkGotoQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = linkGotoQueryRequest.getId();
        Long notId = linkGotoQueryRequest.getNotId();
        String title = linkGotoQueryRequest.getTitle();
        String content = linkGotoQueryRequest.getContent();
        String searchText = linkGotoQueryRequest.getSearchText();
        String sortField = linkGotoQueryRequest.getSortField();
        String sortOrder = linkGotoQueryRequest.getSortOrder();
        List<String> tagList = linkGotoQueryRequest.getTags();
        Long userId = linkGotoQueryRequest.getUserId();
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
     * 获取短链接跳转信息封装
     *
     * @param linkGoto
     * @param request
     * @return
     */
    @Override
    public LinkGotoVO getLinkGotoVO(LinkGoto linkGoto, HttpServletRequest request) {
    // todo: 补充获取短链接跳转信息封装逻辑
        return null;
    }

    /**
     * 分页获取短链接跳转信息封装
     *
     * @param linkGotoPage
     * @param request
     * @return
     */
    @Override
    public Page<LinkGotoVO> getLinkGotoVOPage(Page<LinkGoto> linkGotoPage, HttpServletRequest request) {
        // todo: 补充分页获取短链接跳转信息封装逻辑
        return null;
    }

}
