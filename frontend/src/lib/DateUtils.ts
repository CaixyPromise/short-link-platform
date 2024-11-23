interface DateTimeFormatOptions {
    localeMatcher?: "best fit" | "lookup" | undefined;
    weekday?: "long" | "short" | "narrow" | undefined;
    era?: "long" | "short" | "narrow" | undefined;
    year?: "numeric" | "2-digit" | undefined;
    month?: "numeric" | "2-digit" | "long" | "short" | "narrow" | undefined;
    day?: "numeric" | "2-digit" | undefined;
    hour?: "numeric" | "2-digit" | undefined;
    minute?: "numeric" | "2-digit" | undefined;
    second?: "numeric" | "2-digit" | undefined;
    timeZoneName?: "short" | "long" | "shortOffset" | "longOffset" | "shortGeneric" | "longGeneric" | undefined;
    formatMatcher?: "best fit" | "basic" | undefined;
    hour12?: boolean | undefined;
    timeZone?: string | undefined;
}

export const DEFAULT_DATE_FORMAT = 'YYYY-MM-DD HH:mm:ss';
export const DEFAULT_IOS_DATE_OPTION:DateTimeFormatOptions  = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false // 24小时制
}

export class DateUtils {
    private static isValidDateStr(dateStr: string): boolean {
        return dateStr?.trim()?.length === 0
    }

    /**
     * 将 ISO 日期字符串格式化为指定格式
     * @param dateInput - 输入日期，可以是 Date 或字符串
     * @param formatOptions - 日期格式选项，默认为 "yyyy-MM-dd HH:mm:ss"
     * @param locale - 本地化区域设置，默认为 "zh-CN"
     * @returns 格式化后的日期字符串
     */
    public static formatDateByISO(
        dateInput: Date | string,
        formatOptions?: DateTimeFormatOptions,
        locale = "zh-CN"
    ): string {
        if (typeof dateInput === "string" && this.isValidDateStr(dateInput)) {
            return "";
        }

        const date = typeof dateInput === "string" ? new Date(dateInput) : dateInput;

        if (isNaN(date.getTime())) {
            console.warn("Invalid date input:", dateInput);
            return "";
        }

        const defaultOptions: DateTimeFormatOptions = {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit",
            hour12: false, // 24小时制
        };

        const options = { ...defaultOptions, ...formatOptions };

        // 格式化日期为本地化字符串并替换斜杠为横杠
        return new Intl.DateTimeFormat(locale, options)
            .format(date)
            .replace(/\//g, '-');
    }

    /**
     * 将日期对象转为 ISO 8601 标准字符串
     * @param date - 日期对象
     * @returns ISO 8601 格式的字符串
     */
    public static toISOString(date: Date): string {
        return date.toISOString(); // 返回 ISO 标准格式
    }

    public static formatDate(dateStr: string, format?: string): string {
        if (this.isValidDateStr(dateStr)) {
            return '';
        }
        const date = new Date(dateStr);

        if (!format) {
            format = DEFAULT_DATE_FORMAT
        }

        const map: { [key: string]: string } = {
            YYYY: date.getFullYear().toString(),
            MM: (date.getMonth() + 1).toString().padStart(2, '0'),
            DD: date.getDate().toString().padStart(2, '0'),
            HH: date.getHours().toString().padStart(2, '0'),
            mm: date.getMinutes().toString().padStart(2, '0'),
            ss: date.getSeconds().toString().padStart(2, '0'),
        }
        return format.replace(/YYYY|MM|DD|HH|mm|ss/g, (match) => map[match]);
    }

    public static mergeDateAndTime(dateStr: string, time: {
        hours: string;
        minutes: string;
        seconds: string;
    }): Date {
        // 解析原始日期字符串为 Date 对象
        const date = new Date(dateStr);

        // 设置时间部分
        date.setUTCHours(
            parseInt(time.hours, 10),
            parseInt(time.minutes, 10),
            parseInt(time.seconds, 10),
            0 // 毫秒设置为0
        );

        return date;
    }

    public static mergeDateAndTime_V2(dateObject: Date, time: {
        hours: string;
        minutes: string;
        seconds: string;
    }): Date {
        const [hours, minutes, seconds] = [
            parseInt(time.hours, 10),
            parseInt(time.minutes, 10),
            parseInt(time.seconds, 10),
        ]
        const combined = new Date(dateObject)
        combined.setHours(hours, minutes, seconds, 0)
        return combined
    }
}
