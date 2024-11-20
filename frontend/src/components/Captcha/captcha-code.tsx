import React, { useState, useEffect, useCallback, useRef } from 'react';
import { Captcha } from './captcha-root';
import { Button } from '@/components/ui/button';
import { CaptchaProps, CaptchaRef } from '@/components/Captcha/typing';
import useDebounce from '@/hooks/useDebounce';

export interface CodeCaptchaProps extends CaptchaProps {
    doSend: (value: string) => boolean | Promise<boolean> | undefined;
    cooldownSeconds?: number;
    onCooldownText?: (cooldown: number) => string;
    cooldownText?: string;
}

const CodeCaptcha = React.forwardRef<CaptchaRef, CodeCaptchaProps>(
    ({
        doSend,
        cooldownSeconds = 60,
        cooldownText = '发送验证码',
        onCooldownText,
        inputClassName,
        carrierClassName,
        inputProps,
        ...props
    },
    ref
) => {
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [remainingTime, setRemainingTime] = useState(0);
    const doSendRef = useRef(doSend);


    useEffect(() => {
        if (!doSend) {
            throw new Error('Captcha.Code Error: doSend is required');
        }
        doSendRef.current = doSend;
    }, [doSend]);

    const [debouncedSend] = useDebounce(async () => {
        // 获取输入框的值
        const inputValue = ref?.current?.getValue() || '';

        // 调用 doSend，并传递输入框的值
        const result = await doSendRef.current(inputValue);

        // 根据返回值决定是否启动倒计时和禁用按钮
        if (result || result === undefined) {
            setIsButtonDisabled(true);
            setRemainingTime(cooldownSeconds);
            localStorage.setItem(
                'codeCaptchaCooldown',
                (Date.now() + cooldownSeconds * 1000).toString()
            );
        }
    }, 300);

    useEffect(() => {
        const storedTime = localStorage.getItem('codeCaptchaCooldown');
        if (storedTime) {
            const timeLeft = parseInt(storedTime, 10) - Date.now();
            if (timeLeft > 0) {
                setRemainingTime(Math.ceil(timeLeft / 1000));
                setIsButtonDisabled(true);
            } else {
                setIsButtonDisabled(false);
            }
        } else {
            setIsButtonDisabled(false);
        }
    }, []);

    useEffect(() => {
        if (remainingTime > 0) {
            const timer = setTimeout(() => {
                setRemainingTime((prevTime) => prevTime - 1);
                if (remainingTime === 1) {
                    setIsButtonDisabled(false);
                    localStorage.removeItem('codeCaptchaCooldown');
                } else {
                    localStorage.setItem(
                        'codeCaptchaCooldown',
                        (Date.now() + (remainingTime - 1) * 1000).toString()
                    );
                }
            }, 1000);
            return () => clearTimeout(timer);
        }
    }, [remainingTime]);

    const handleSend = useCallback(() => {
        if (!isButtonDisabled) {
            debouncedSend();
        }
    }, [isButtonDisabled, debouncedSend]);

    const handleCooldownText = (cooldown: number) => {
        if (onCooldownText) {
            return onCooldownText(cooldown);
        } else {
            return `重新发送 ${cooldown} 秒`;
        }
    };

    return (
        <Captcha {...props} inputProps={inputProps} ref={ref} inputClassName={inputClassName} carrierClassName={carrierClassName}>
            <Button
                onClick={handleSend}
                disabled={isButtonDisabled}
                className="w-full bg-primary hover:bg-primary/90 text-primary-foreground"
            >
                {isButtonDisabled ? handleCooldownText(remainingTime) : cooldownText}
            </Button>
        </Captcha>
    );
});

CodeCaptcha.displayName = 'Captcha.Code';

export { CodeCaptcha };
