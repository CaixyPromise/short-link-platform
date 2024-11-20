import React, {Dispatch} from "react";

// 页面属性
export interface LoginPageProps
{

}
// 登录表单组件
export interface LoginFormProps
{
    email: string,
    setEmail: React.setStateAction<string>,
}

export type OAuthProviderType = {
    name: string;
    icon: React.ReactNode;
    url: string;
}
// 注册表单组件
// export interface RegisterFormProps
// {
// }


// 表单登录状态
export interface LoginPayloadType
{
    password: string,
    captcha?: string
}