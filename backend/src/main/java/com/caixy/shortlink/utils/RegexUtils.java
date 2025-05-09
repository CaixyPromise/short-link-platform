package com.caixy.shortlink.utils;

import com.caixy.shortlink.constant.RegexPatternConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配工具类
 *
 * @name: com.caixy.shortlink.utils.RegexUtils
 * @author: CAIXYPROMISE
 * @since: 2024-04-26 20:08
 **/
public class RegexUtils
{
    public static boolean validUrl(String url)
    {
        return match(RegexPatternConstants.URL_REGEX, url);
    }

    /**
     * 加密文字
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/14 下午10:27
     */
    public static String encryptText(String text, String regex, String replaceText)
    {
        if (text.isEmpty())
        {
            return "";
        }
        return text.replaceAll(regex, replaceText);
    }

    /**
     * 校验手机号格式
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 20:56
     */
    public static boolean isMobilePhone(String input)
    {
        return match(RegexPatternConstants.PHONE_REGEX, input);
    }

    /**
     * 校验邮箱格式
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 20:55
     */
    public static boolean isEmail(String input)
    {
        return match(RegexPatternConstants.EMAIL_REGEX, input);
    }

    /**
     * 校验密码合法性
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 20:58
     */
    public static boolean validatePassword(String input)
    {
        return match(RegexPatternConstants.PASSWORD_REGEX, input);
    }

    /**
     * 提取Json
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/4/26 下午8:14
     */
    public static String extraJson(String jsonString)
    {
        Matcher matcher = RegexPatternConstants.EXTRA_JSON_PATTERN.matcher(jsonString);
        if (matcher.find())
        {
            return matcher.group(0);
        }
        else
        {
            return "{}";
        }
    }

    /**
     * 校验名称是否可用
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/5/19 下午1:43
     */
    public static boolean validatedUserName(String userName)
    {
        return match(RegexPatternConstants.NAME_REGEX, userName);
    }

    /**
     * 匹配账号是否合法
     */
    public static boolean validateAccount(String userAccount)
    {
        return match(RegexPatternConstants.ACCOUNT_REGEX, userAccount);
    }

    private static boolean match(String regex, String input)
    {
        if (input == null || input.isEmpty())
        {
            return false;
        }
        return Pattern.matches(regex, input);
    }

}
