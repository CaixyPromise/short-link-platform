package com.caixy.shortlink.model.dto.linkGoto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑短链接跳转信息请求
 * @author: CAIXYPROMISE
 */
@Data
public class LinkGotoEditRequest implements Serializable {

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