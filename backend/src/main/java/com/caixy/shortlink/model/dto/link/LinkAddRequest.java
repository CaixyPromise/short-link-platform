package com.caixy.shortlink.model.dto.link;

import com.caixy.shortlink.constant.RegexPatternConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 创建短链接信息请求
 *
 * @author: CAIXYPROMISE
 */
@Data
public class LinkAddRequest implements Serializable
{
    /**
     * 原始链接
     */
    @NotBlank
    @Pattern(regexp = RegexPatternConstants.URL_REGEX,
            message = "域名格式不正确")
    private String originUrl;

    /**
     * 分组标识
     */
    @NotBlank
    @Size(max = 32, message = "分组标识错误")
    private String gid;

    /**
     * 短链接名称
     */
    @NotBlank
    @Size(max = 32, message = "短链接名称长度不能大于32")
    private String linkName;

    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    @NotNull
    @Max(value = 1, message = "有效期类型不正确")
    @Min(value = 0, message = "有效期类型不正确")
    private Integer validDateType;

    /**
     * 有效期-开始时间
     */
    private Date validDateStart;
    /**
     * 有效期-结束时间
     */
    private Date validDateEnd;

    /**
     * 描述
     */
    @Size(max = 1024, message = "描述长度不能超过1024")
    private String describe;

    @Serial
    private static final long serialVersionUID = 1L;
}