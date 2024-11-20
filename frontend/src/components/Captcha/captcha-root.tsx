'use client'

import React, { forwardRef, useImperativeHandle, useState } from 'react'
import { Input } from "@/components/ui/input"
import { CaptchaProps, CaptchaRef } from "@/components/Captcha/typing";
import {ImageCaptchaProps} from "@/components/Captcha/captcha-image";
import {CodeCaptchaProps} from "@/components/Captcha/captcha-code";

interface ExtendedCaptchaProps extends CaptchaProps {
    children?: React.ReactNode;
    refreshCaptcha?: () => Promise<void>;

}

const Captcha = forwardRef<CaptchaRef, ExtendedCaptchaProps>(
    ({
    value,
    onValueChange,
    inputProps,
    placeholder = "请输入验证码",
    captchaId,
    children,
    refreshCaptcha,
    inputClassName,
    carrierClassName
}, ref) => {
    const [inputValue, setInputValue] = useState<string>(value || "")

    useImperativeHandle(ref, () => ({
        getValue: () => inputValue,
        getCaptchaId: () => captchaId,
        refreshCaptcha
    }))

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value
        setInputValue(value)
        onValueChange?.(value)
    }

    return (
        <div className="flex items-center w-full">
            <Input
                type="text"
                placeholder={placeholder}
                value={inputValue}
                onChange={handleInputChange}
                className={`flex-[6] ${inputClassName || ''}`}
                {...inputProps}
            />
            <div className={`flex-[4] ml-2 ${carrierClassName || ''}`}>
                {children}
            </div>
        </div>
    )
})

Captcha.displayName = 'Captcha'

// 定义子组件类型
interface CaptchaComponent
    extends React.ForwardRefExoticComponent<CaptchaProps & React.RefAttributes<CaptchaRef>> {
    Image: React.ForwardRefExoticComponent<ImageCaptchaProps & React.RefAttributes<CaptchaRef>>;
    Code: React.ForwardRefExoticComponent<CodeCaptchaProps & React.RefAttributes<CaptchaRef>>;
}

// 强制转换类型以包含子组件
const CaptchaExport = Captcha as CaptchaComponent;

export { CaptchaExport as Captcha };
