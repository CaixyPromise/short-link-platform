package com.caixy.shortlink.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短链创建类型
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/20 2:03
 */
@Getter
@AllArgsConstructor
public enum ShortLinkCreateType
{
    /**
     * 接口创建
     */
    SDK(0, "接口创建"),
    /**
     * 控制台创建
     */
    CONSOLE(1, "控制台创建"),
    ;
    private final Integer code;
    private final String desc;

    public static ShortLinkCreateType getEnumByCode(Integer code)
    {
        if (code == null)
        {
            return null;
        }
        for (ShortLinkCreateType value : ShortLinkCreateType.values())
        {
            if (code.equals(value.getCode()))
            {
                return value;
            }
        }
        return null;
    }
}
