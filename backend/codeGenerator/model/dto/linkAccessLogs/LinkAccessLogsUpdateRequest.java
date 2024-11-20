package com.caixy.shortlink.model.dto.linkAccessLogs;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新短链接访问日志请求
 * @author: CAIXYPROMISE
 */
@Data
public class LinkAccessLogsUpdateRequest implements Serializable {

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