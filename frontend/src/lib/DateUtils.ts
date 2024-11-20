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
    public static formatDateByISO(isoString: string, iosOption?: DateTimeFormatOptions): string {
        if (this.isValidDateStr(isoString)) {
            return ''
        }
        const date = new Date(isoString)
        return new Intl.DateTimeFormat('zh-CN', iosOption && DEFAULT_IOS_DATE_OPTION).format(date).replace(/\//g, '-')

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
}
