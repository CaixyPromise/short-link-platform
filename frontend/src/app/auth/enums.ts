
// 表单状态
export class FormStateEnum {
    private static allValues: FormStateEnum[] = [];

    static readonly LOGIN = new FormStateEnum('login', "登录");
    static readonly REGISTER = new FormStateEnum('register', "注册账号");
    static readonly FORGET = new FormStateEnum('forget', "找回密码");
    static readonly ACTIVATE = new FormStateEnum('activate', "激活账号");

    private constructor(private readonly code: string, private readonly description: string) {
        FormStateEnum.allValues.push(this);
    }
    static getByCode(value: string | undefined | null): FormStateEnum | null {
        if (value === undefined) {
            return null;
        }
        return FormStateEnum.allValues.find(state => state.code === value) || null;
    }

    getCode(): string {
        return this.code;
    }

    getDescription(): string {
        return this.description;
    }

    toString(): string {
        return `${this.description} (${this.code})`;
    }
}