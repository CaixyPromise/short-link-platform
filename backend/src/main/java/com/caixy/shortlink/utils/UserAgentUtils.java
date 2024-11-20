package com.caixy.shortlink.utils;


import eu.bitwalker.useragentutils.UserAgent;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户Agent工具类
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.utils.UserAgentUtils
 * @since 2024/10/28 02:47
 */
public class UserAgentUtils
{

    /**
     * 获取浏览器类型
     */
    public static String getBrowser(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        return userAgent.getBrowser().getName();
    }

    /**
     * 获取操作系统类型
     */
    public static String getOS(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        return userAgent.getOperatingSystem().getName();
    }
}
