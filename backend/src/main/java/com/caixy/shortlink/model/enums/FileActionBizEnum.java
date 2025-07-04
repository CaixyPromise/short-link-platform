package com.caixy.shortlink.model.enums;

import com.caixy.shortlink.constant.FileTypeConstant;
import com.caixy.shortlink.utils.SizeUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传业务类型枚举
 */
@Getter
public enum FileActionBizEnum
{

    USER_AVATAR(
            "用户头像",
             "avatar",
            FileTypeConstant.AVATAR,
            0,
            SizeUtils.of(2, SizeUtils.SizeType.MB),
            new HashSet<>(Arrays.asList("jpeg", "jpg", "svg", "png", "webp")),
            SaveFileMethodEnum.SAVE_LOCAL)
    ;
    /**
     * 用途说明
     */
    private final String text;

    /**
     * 文件的路由静态访问路由
     */
    private final String routePath;

    /**
     * 文件上传业务类型
     */
    private final String label;

    /**
    * 上传业务code唯一值
    */
    private final Integer value;

    /**
     * 文件上传业务类型最大限制
     */
    private final SizeUtils.ByteSize maxSize;

    /**
     * 允许的文件扩展名集合
     */
    private final Set<String> fileSuffix;

    /**
     * 文件保存方式
     */
    private final SaveFileMethodEnum saveFileMethod;

    @JsonValue
    public Integer getValue() {
        return value;
    }

    @JsonCreator
    public static FileActionBizEnum fromJson(Integer value) {
        FileActionBizEnum enumByValue = getEnumByValue(value);
        if (enumByValue == null) {
            throw new IllegalArgumentException("Invalid value: " + value);
        }
        return enumByValue;
    }


    FileActionBizEnum(String text,
                      String routePath,
                      String label,
                      Integer value,
                      SizeUtils.ByteSize maxSize,
                      Set<String> fileSuffix,
                      SaveFileMethodEnum saveFileMethod
                      )
    {
        this.text = text;
        this.routePath = routePath;
        this.label = label;
        this.value = value;
        this.maxSize = maxSize;
        this.fileSuffix = fileSuffix;
        this.saveFileMethod = saveFileMethod;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileActionBizEnum getEnumByValue(Integer value)
    {
        if (ObjectUtils.isEmpty(value))
        {
            return null;
        }
        for (FileActionBizEnum anEnum : FileActionBizEnum.values())
        {
            if (anEnum.value.equals(value))
            {
                return anEnum;
            }
        }
        return null;
    }


    /**
     * 构建文件保存路径+名称
     *
     * @param userId   用户ID
     * @param fileName 文件名称
     * @return 文件路径
     */
    public Path buildFileAbsolutePathAndName(Long userId, String fileName)
    {
        // /{value}/{userId}/{fileName}
        return Paths.get(label, userId.toString(), fileName);
    }

    /**
     * 构建文件保存文件的路径
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/6/30 下午7:46
     */
    public Path buildFilePath(Long userId)
    {
        // /{value}/{userId}
        // 格式：/attachment/12345/
        return Paths.get(label, userId.toString());
    }


    /**
     * 构建文件访问URL
     *
     * @param userId   用户ID
     * @param fileName 文件名称
     * @return 文件访问URL
     */
    public String buildFileURL(Long userId, String fileName)
    {
        // /{routePath}/{userId}/{fileName}
        // 格式：/avatar/12345/8d2f03a7-md5hash12345.jpg
        return String.format("%s/%s/%s", routePath, userId, fileName);
    }
}
