package com.caixy.shortlink.model.dto.file;

import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    private FileActionBizEnum biz;

    /**
    * 上传token
    */
    @NotEmpty
    @Size(max = 36) // uuid
    private String token;

    /**
    * 文件名称，仅用在秒传时传递，用于自定义文件名称
    */
    @NotEmpty
    @Size(max = 128)
    private String fileName;


    /**
    * 文件签名，仅用在秒传时传递，用于校验文件是否一致
    */
    @Schema(description = "文件挑战签名")
    private String signature;

    /**
    * 防重放字段
    */
    @NotEmpty
    @Size(max = 36) // uuid长度
    @Schema(description = "防重放标识，每次请求必须唯一")
    private String nonce;

    /**
    * 上传触发时时间戳（秒）
    */
    @NotNull
    @Schema(description = "时间戳（秒），用于防重放攻击")
    private Long timestamp;



    private static final long serialVersionUID = 1L;
}