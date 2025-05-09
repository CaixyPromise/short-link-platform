package com.caixy.shortlink.model.dto.linkBrowserStats;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建短链接浏览器统计请求
 * @author: CAIXYPROMISE
 */
@Data
public class LinkBrowserStatsAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}