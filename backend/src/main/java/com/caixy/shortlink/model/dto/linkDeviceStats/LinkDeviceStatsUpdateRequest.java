package com.caixy.shortlink.model.dto.linkDeviceStats;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新短链接设备统计请求
 * @author: CAIXYPROMISE
 */
@Data
public class LinkDeviceStatsUpdateRequest implements Serializable {

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