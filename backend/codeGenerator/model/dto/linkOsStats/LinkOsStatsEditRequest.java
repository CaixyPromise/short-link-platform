package com.caixy.shortlink.model.dto.linkOsStats;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑短链接操作系统统计请求
 * @author: CAIXYPROMISE
 */
@Data
public class LinkOsStatsEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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