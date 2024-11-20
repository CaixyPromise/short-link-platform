export type RuleType = 'string' | 'number' | 'boolean' |
    'method' | 'regexp' | 'integer' |
    'float' | 'object' | 'enum' |
    'date' | 'url' | 'hex' | 'email';
export type StoreValue = any;

interface BaseRule
{
    warningOnly?: boolean;
    enum?: StoreValue[];
    len?: number;
    max?: number;
    message: string;
    min?: number;
    pattern: RegExp;
    required?: boolean;
    transform?: (value: StoreValue) => StoreValue;
    type?: RuleType;
    whitespace?: boolean;
    validateTrigger?: string | string[];
}


export class RegexPattern {
    public static readonly EMAIL_REGEX: BaseRule = {
        pattern: /[\w!#$%&'*+\/=?^_`{|}~-]+(?:\.[\w!#$%&'*+\/=?^_`{|}~-]+)*@(?:\w(?:[\w-]*\w)?\.)+\w(?:[\w-]*\w)?/,
        message: "请输入有效的邮箱",
    };

    public static readonly ACCOUNT_REGEX: BaseRule = {
        pattern: /^[a-zA-Z][a-zA-Z0-9]{4,15}$/,
        message: "用户登录账号必须以字母开头，长度在5到16字节之间，且只允许包含字母和数字（不允许下划线"
    };

    public static readonly NAME_REGEX: BaseRule = {
        pattern: /^[\u4e00-\u9fa5a-zA-Z]{1,30}$/,
        message: "姓名必须为中文或英文，长度在1到30之间"
    };

    public static readonly PASSWORD_REGEX: BaseRule = {
        pattern: /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$/,
        message: "密码必须包含大小写字母和数字，长度在8到20之间"
    };

    public static readonly PHONE_REGEX: BaseRule = {
        pattern: /^1[3-9]\d{9}$/,
        message: "请输入有效的手机号",
    };

    public static readonly URL_REGEX: BaseRule = {
        pattern: /^(https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s]{2,}|www\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s]{2,}|https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9]+\.[^\s]{2,}|www\.[a-zA-Z0-9]+\.[^\s]{2,})$/,
        message: "请输入有效的网址"
    };

    private BASE_RULE: BaseRule;
    public constructor(pattern: BaseRule)
    {
        this.BASE_RULE = pattern;
    }

    public test(text: string): boolean
    {
        return this.BASE_RULE.pattern.test(text.trim());
    }

    public static testOf(text: string, rule: BaseRule): boolean
    {
        return rule.pattern.test(text.trim());
    }
}

export class RegexUtil
{
    static testOf = (text: string, rule: BaseRule) =>
    {
        return rule.pattern?.test(text.trim());
    }
}
