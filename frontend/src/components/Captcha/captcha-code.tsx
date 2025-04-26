import React, { useState, useEffect, useCallback, useRef, forwardRef, useImperativeHandle } from 'react';
import { Captcha } from './captcha-root';
import { Button } from '@/components/ui/button';
import { CaptchaProps, CaptchaRef } from '@/components/Captcha/typing';
import useDebounce from '@/hooks/useDebounce';

export interface CodeCaptchaProps extends CaptchaProps {
	doSend: (value: string) => boolean | Promise<boolean>;
	cooldownSeconds?: number;
	onCooldownText?: (cooldown: number) => string;
	cooldownText?: string;
	buttonClass?: string;
	value?: string;
	onChange?: (value: string) => void;
}

export interface CodeCaptchaRef extends CaptchaRef {
	isSend?: boolean;
	setSendStatus: (isSent: boolean) => void; // 外部调用时不需要传入时间
	clear?: () => void;
}

const CodeCaptcha = forwardRef<CodeCaptchaRef, CodeCaptchaProps>(
	({
		 doSend,
		 cooldownSeconds = 60,
		 cooldownText = '发送验证码',
		 onCooldownText,
		 inputClassName,
		 carrierClassName,
		 inputProps,
		 buttonClass,
		value,
		onChange,
		 ...props
	 },
	 ref
	) => {
		const [isButtonDisabled, setIsButtonDisabled] = useState(false);
		const [remainingTime, setRemainingTime] = useState(0);
		const doSendRef = useRef(doSend);

		useEffect(() => {
			if (!doSend) {
				throw new Error('Captcha.Code Error: doSend is required');
			}
			doSendRef.current = doSend;
		}, [doSend]);

		// 发送验证码并进入倒计时
		const [debouncedSend] = useDebounce(async () => {
			const inputValue = ref?.current?.getValue() || '';

			const result = await doSendRef.current(inputValue);

			if (result || result === undefined) {
				setSendStatus(true);
			}
		}, 300);

		// 外部可控的发送状态方法
		const setSendStatus = useCallback((isSent: boolean) => {
			if (isSent) {
				setIsButtonDisabled(true);
				setRemainingTime(cooldownSeconds); // 使用内部默认时间
				localStorage.setItem('codeCaptchaCooldown', (Date.now() + cooldownSeconds * 1000).toString());
			} else {
				setIsButtonDisabled(false);
				setRemainingTime(0);
				localStorage.removeItem('codeCaptchaCooldown');
			}
		}, [cooldownSeconds]);

		// 初始化检查是否有未完成的倒计时
		useEffect(() => {
			const storedTime = localStorage.getItem('codeCaptchaCooldown');
			if (storedTime) {
				const timeLeft = Math.ceil((parseInt(storedTime, 10) - Date.now()) / 1000);
				if (timeLeft > 0) {
					setSendStatus(true); // 使用内部倒计时时间
				} else {
					setSendStatus(false);
				}
			}
		}, [setSendStatus]);

		// 处理倒计时逻辑
		useEffect(() => {
			if (remainingTime > 0) {
				const timer = setInterval(() => {
					setRemainingTime((prevTime) => {
						if (prevTime === 1) {
							setSendStatus(false);
							return 0;
						}
						return prevTime - 1;
					});
				}, 1000);
				return () => clearInterval(timer);
			}
		}, [remainingTime, setSendStatus]);  // 仅监听 remainingTime 和 setSendStatus

		// 发送验证码按钮
		const handleSend = useCallback(() => {
			if (!isButtonDisabled) {
				debouncedSend();
			}
		}, [isButtonDisabled, debouncedSend]);

		// 计算倒计时文本
		const handleCooldownText = (cooldown: number) => {
			return onCooldownText ? onCooldownText(cooldown) : `重新发送 ${cooldown} 秒`;
		};

		// 通过 ref 暴露方法
		useImperativeHandle(ref, () => ({
			...ref?.current,
			setSendStatus: (isSent: boolean) => {
				setSendStatus(isSent); // 外部调用时不需要传入时间
			},
			clear: () => {
				setSendStatus(false);
				localStorage.removeItem("codeCaptchaCooldown")
			}

		}));

		return (
			<Captcha {...props} inputProps={inputProps} ref={ref} inputClassName={inputClassName}
			         carrierClassName={carrierClassName} value={value} onChange={onChange}>
				<Button
					type="button"
					onClick={handleSend}
					disabled={isButtonDisabled}
					className={buttonClass ?? "w-full bg-primary hover:bg-primary/90 text-primary-foreground"}
				>
					{isButtonDisabled ? handleCooldownText(remainingTime) : cooldownText}
				</Button>
			</Captcha>
		);
	}
);

CodeCaptcha.displayName = 'Captcha.Code';

export { CodeCaptcha };
