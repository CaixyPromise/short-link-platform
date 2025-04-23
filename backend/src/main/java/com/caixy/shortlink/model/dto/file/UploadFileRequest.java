package com.caixy.shortlink.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 */
@Data
public class UploadFileRequest implements Serializable
{
    /**
     * 业务
     */
    private Integer biz;

    /**
    * 上传token
    */
    private String token;


    private static final long serialVersionUID = 1L;
}