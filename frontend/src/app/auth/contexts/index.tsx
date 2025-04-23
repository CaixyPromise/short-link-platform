'use client'
import React, {createContext, useContext, useEffect, useState} from "react";
import {useSearchParams} from "next/navigation";
import {ReadonlyURLSearchParams} from "next/dist/client/components/navigation.react-server";
import {FormStateEnum} from "@/app/auth/enums";


export type AuthPageContextType = {
	userAccount: string,
	setUserAccount: (userAccount: string) => void,
	authState: FormStateEnum,
	setAuthState: (authState: FormStateEnum) => void,
	searchParams?: ReadonlyURLSearchParams
};


const AuthPageContext = createContext<AuthPageContextType>({
	userAccount: '',
	setUserAccount: () => null,
	authState: FormStateEnum.LOGIN,
	setAuthState: (authState: FormStateEnum) => FormStateEnum.LOGIN,
});
export const AuthPageProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
	const searchParams = useSearchParams()
	const [authState, setAuthState] = useState<FormStateEnum>(FormStateEnum.LOGIN)
	const [userAccount, setUserAccount] = useState<string>('')

	useEffect(() => {
		const code = searchParams.get("code")
		if (code) {
			setAuthState(FormStateEnum.ACTIVATE)
		}
	}, [searchParams])

	return (
		<AuthPageContext.Provider value={{ userAccount, setUserAccount, authState, setAuthState, searchParams }}>
			{children}
		</AuthPageContext.Provider>
	)
}

export const useAuthPageData = () => {
	const context = useContext(AuthPageContext);
	if (context === undefined) {
		throw new Error('useLoginFormData must be used within a LoginFormProvider');
	}
	return context;
}