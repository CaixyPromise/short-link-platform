package com.caixy.shortlink.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具类
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.utils.TimeUtils
 * @since 2024/10/26 01:00
 */
public class TimeUtils
{
    /**
     * 获取时间单位对应的毫秒数
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/26 上午1:02
     */
    public static Long getMills(int quantity, TimeUnit timeUnit)
    {
        return timeUnit.toMillis(quantity);
    }

    /**
     * 判断目标时间是否早于参考时间
     *
     * @param targetDate    目标时间
     * @param referenceDate 参考时间
     * @return 如果目标时间早于参考时间，返回 true；否则返回 false
     */
    public static boolean isBefore(Date targetDate, Date referenceDate)
    {
        if (targetDate == null || referenceDate == null)
        {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return targetDate.before(referenceDate);
    }

    /**
     * 判断目标时间是否晚于参考时间
     *
     * @param targetDate    目标时间
     * @param referenceDate 参考时间
     * @return 如果目标时间晚于参考时间，返回 true；否则返回 false
     */
    public static boolean isAfter(Date targetDate, Date referenceDate)
    {
        if (targetDate == null || referenceDate == null)
        {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return targetDate.after(referenceDate);
    }

    /**
     * 判断目标时间是否晚于当前时间
     *
     * @param targetDate 目标时间
     * @return 如果目标时间晚于当前时间，返回 true；否则返回 false
     */
    public static boolean isAfterNow(Date targetDate)
    {
        if (targetDate == null)
        {
            throw new IllegalArgumentException("Target date cannot be null");
        }
        Date now = new Date();
        return targetDate.after(now);
    }

    /**
     * 判断目标时间是否早于当前时间
     *
     * @param targetDate 目标时间
     * @return 如果目标时间早于当前时间，返回 true；否则返回 false
     */
    public static boolean isBeforeNow(Date targetDate)
    {
        if (targetDate == null)
        {
            throw new IllegalArgumentException("Target date cannot be null");
        }
        Date now = new Date();
        return targetDate.before(now);
    }

    /**
     * 判断目标时间是否处于指定时间区间内（包含边界）
     *
     * @param targetDate 目标时间
     * @param startDate  区间开始时间
     * @param endDate    区间结束时间
     * @return 如果目标时间处于区间内，返回 true；否则返回 false
     */
    public static boolean isWithinRange(Date targetDate, Date startDate, Date endDate)
    {
        if (targetDate == null || startDate == null || endDate == null)
        {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return !targetDate.before(startDate) && !targetDate.after(endDate);
    }
}
