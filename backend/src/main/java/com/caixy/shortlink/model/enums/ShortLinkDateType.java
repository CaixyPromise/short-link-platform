package com.caixy.shortlink.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短链日期类型
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/20 2:01
 */
@Getter
@AllArgsConstructor
public enum ShortLinkDateType
{
    /**
     * 永久有效
     */
    PERMANENT(0, "永久有效"),
    /**
     * 自定义
     */
    CUSTOM(1, "自定义");
    ;
    private final Integer code;
    private final String desc;

    public static ShortLinkDateType getEnumByCode(Integer code)
    {
        if (code == null)
        {
            return null;
        }
        for (ShortLinkDateType dateType : ShortLinkDateType.values())
        {
            if (dateType.getCode().equals(code))
            {
                return dateType;
            }
        }
        return null;
    }
}
