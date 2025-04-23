package com.caixy.shortlink.model.dto.file;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 检查文件是否存在返回体
 *
 * @Author CAIXYPROMISE
 * @since 2025/4/22 21:40
 */
@Data
@Builder
public class CheckFileExistResponse implements Serializable
{
    /**
    * 上传token / 对应文件信息token
    */
    private String token;

    /**
    * 是否存在
    */
    private Boolean exist;

    private static final long serialVersionUID = 1L;
}
