package com.caixy.shortlink.constant;

/**
 * 通用常量
 */
public interface CommonConstant
{
    String SDK_USER_KEY = "SDK_USER";
    /**
     * 软删除符号(未删除)
     */
    Integer NOT_DELETE_FLAG = 0;
    /**
     * 软删除符号(已删除)
     */
    Integer DELETED_FLAG = 1;

    /**
     * 启用状态(启用)
     */
    Integer ENABLE_STATUS = 0;
    /**
     * 未启用状态
     */
    Integer DISABLE_STATUS = 1;
    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    String GBK = "GBK";

    /**
     * www主域
     */
    String WWW = "www.";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";
    
    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    /**
     * 默认时间格式
     */
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 验证码session-key
     */
    String CAPTCHA_SIGN = "captcha";

    String FRONTED_URL = "http://localhost:3000";
    String BACKEND_URL = "http://localhost:9998";

    String URL_SUFFIX_SEPARATOR = "/";

    /**
     * ip地区接口地址
     */
    String IP_REMOTE_URL = "https://api.vore.top/api/IPdata";
}
