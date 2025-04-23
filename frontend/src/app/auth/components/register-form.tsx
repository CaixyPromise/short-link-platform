"use client"

import React, {useRef} from "react"
import { useForm } from "react-hook-form"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import {OAuthOptions} from "@/app/auth/components/oauth-options"
import {Captcha, CaptchaRef} from "@/components/Captcha";
import {userRegister} from "@/api/authController";
import {ResultCode} from "@/enums/ResultCodeEnum";
import {useToast} from "@/hooks/use-toast";
import {useAuthPageData} from "@/app/auth/contexts";
import {FormContainer} from "@/app/auth/components/form-container";
import {FormStateEnum} from "@/app/auth/enums";
import {queryServer} from "@/app/auth/server";
import {useAppSelector} from "@/stores/hooks";
import SystemConfig from "@/stores/SystemConfig";

interface RegisterForm {
	email: string;
	username: string;
	captcha: string;
	password: string
	confirmPassword: string
}

export function RegisterForm() {
	const {toast} = useToast();
	const {setAuthState} = useAuthPageData();
	const imageCodeRef = useRef<CaptchaRef>(null);
	const registerForm = useForm<RegisterForm>()
	const {title} = useAppSelector(state => state.SystemConfig)
	const checkInfoSubmit = async (data: RegisterForm) => {
		try {
			const response = await userRegister({
				userName: data.username,
				userEmail: data.email,
				captchaId: imageCodeRef.current?.getCaptchaId(),
				captcha: imageCodeRef?.current?.getValue()
			})
			if (response?.code === ResultCode.SUCCESS) {
				toast({
					title: "注册成功",
					description: "请前往邮箱验证",
					position: 'bottom-right',
					variant: 'default',
				})
				setAuthState(FormStateEnum.LOGIN)
			}
		} catch (e: Error) {
			toast({
				title: "注册失败",
				description: e.message,
				position: 'bottom-right',
				variant: 'destructive',
			})
		}
	}

	return (
		<FormContainer>
			<div className="mb-8 text-center">
				<div className="inline-block p-3 mb-4 rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600">
					<span className="text-2xl font-bold text-white">AA</span>
				</div>
				<h2 className="text-2xl font-semibold text-gray-800">{title}-{FormStateEnum.REGISTER.getDescription()}</h2>
			</div>

			<form onSubmit={registerForm.handleSubmit(checkInfoSubmit)} className="space-y-6">
				<div className="space-y-2">
					<Label htmlFor="email">邮箱</Label>
					<Input
						id="email"
						type="email"
						className="bg-white/50 border-gray-300"
						{...registerForm.register("email", {required: true})}
					/>
				</div>

				<div className="space-y-2">
					<Label htmlFor="username">用户名</Label>
					<Input
						id="username"
						className="bg-white/50 border-gray-300"
						{...registerForm.register("username", {required: true})}
					/>
				</div>

				<div className="space-y-2">
					<Label htmlFor="captcha">验证码</Label>
					<Captcha.Image
						inputClassName='bg-white/50 border-gray-300'
						fetchImage={async () => await queryServer.captchaImage() as API.CaptchaVO}
						ref={imageCodeRef}
					/>
				</div>

				<Button type="submit" className="w-full bg-indigo-600 hover:bg-indigo-700 text-white">
					注册
				</Button>
			</form>

			<div className="mt-6 text-center text-sm text-gray-600">
				<span>已有帐号?</span>
				<Button variant="link" onClick={()=>setAuthState(FormStateEnum.LOGIN)} className="text-indigo-600 hover:text-indigo-800">
					去登录
				</Button>
			</div>

			<OAuthOptions/>
		</FormContainer>
	)
}

