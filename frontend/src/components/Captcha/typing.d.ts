import React from "react";

export interface CaptchaProps {
    className?: string;
    value?: string;
    onValueChange?: (value: string) => void;
    onInputChange?: (value: string) => void; // 如果不再需要，可以移除
    captchaId?: string | null | undefined;
    placeholder?: string;
    inputClassName?: string;
    carrierClassName?: string;
    inputProps?: React.InputHTMLAttributes<HTMLInputElement>;
}

export interface CaptchaRef {
    getValue: () => string;
    getCaptchaId: () => string | undefined | null;
    refreshCaptcha?: () => void;
}
