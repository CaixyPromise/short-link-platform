'use client'

import React, {createContext, useCallback, useContext, useState} from "react";
import {getCaptcha} from "@/api/captchaController";

export interface CaptchaContextProps {
	captchaId: string | null;
	captchaValue: string;
	captchaImage: string;
	setCaptchaValue: (value: string) => void;
	refreshCaptcha: () => void;
}

const CaptchaContext = createContext<CaptchaContextProps>({} as CaptchaContextProps);

export const CaptchaContextProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
	const [captchaId, setCaptchaId] = useState<string>('');
	const [captchaValue, setCaptchaValue] = useState<string>('');
	const [captchaImage, setCaptchaImage] = useState<string>('');

	const fetchCaptcha = useCallback(async () => {
		try {
			const {code, data} = await getCaptcha();
			if (code === 0) {
				setCaptchaImage(data?.codeImage)
				setCaptchaId(data?.uuid)
			}
		} catch (e: Error) {
			console.log(e.message)
		}
	}, []);
	const refreshCaptcha = useCallback(async () => {
		await fetchCaptcha();
	}, [fetchCaptcha]);

	return (
		<CaptchaContext.Provider value={{
			captchaId,
			captchaValue,
			captchaImage,
			setCaptchaValue,
			refreshCaptcha
		}}>
			{children}
		</CaptchaContext.Provider>
	)
}

export const useCaptcha = () => {
	const context = useContext(CaptchaContext);
	if (!context) {
		throw new Error('useCaptcha must be used within a CaptchaContextProvider');
	}
	return context;
}