package com.caixy.shortlink.manager.file.domain;

import cn.hutool.core.util.RandomUtil;
import com.caixy.shortlink.utils.JsonUtils;
import com.caixy.shortlink.utils.hmac.domain.HmacPayload;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 文件HMAC信息类
 *
 * @Author CAIXYPROMISE
 * @since 2025/6/27 1:34
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FileHmacInfo extends HmacPayload
{
    /**
     * 切片起始偏移
     */
    @Schema(description = "切片起始偏移")
    private long offset;
    /**
     * 切片长度
     */
    @Schema(description = "切片长度")
    private int length;


    public static FileHmacInfo build(long fileSize)
    {
        int maxChunk = 4 * 1024;
        int chunkLen = (int) Math.min(maxChunk, fileSize);
        // 随机生成一个偏移量，用于切片
        long maxOffset = fileSize - chunkLen;
        long offset = maxOffset > 0 ? ThreadLocalRandom
                .current()
                .nextLong(0, maxOffset + 1) : 0L;

        // 生成通用随机字段
        HmacPayload base = HmacPayload.build();

        return FileHmacInfo
                .builder()
                .nonce(base.getNonce())
                .challenge(base.getChallenge())
                .timestamp(base.getTimestamp())
                .salt(base.getSalt())
                // 子类字段
                .offset(offset)
                .length(RandomUtil.randomInt(0, chunkLen))
                .build();
    }

    public static void main(String[] args)
    {
        System.out.println(JsonUtils.toJsonString(FileHmacInfo.build(1024 * 1024 * 1024)));
    }
}