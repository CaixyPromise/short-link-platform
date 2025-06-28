package com.caixy.shortlink.model.dto.file;

import com.caixy.shortlink.manager.file.domain.FileHmacInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 检查文件是否存在返回体
 *
 * @Author CAIXYPROMISE
 * @since 2025/4/22 21:40
 * 
 */
@Data
@Builder
@Schema(description = "检查文件是否存在返回体")
public class CheckFileExistResponse implements Serializable
{
    /**
     * 上传token / 对应文件信息token
     */
    @Schema(description = "上传token")
    private String token;

    /**
    * 文件秒传核心信息，仅在秒传时设置
    */
    @Schema(description = "文件秒传核心信息，仅在秒传时设置")
    private FileHmacInfo challenge;

    private static final long serialVersionUID = 1L;
}
