'use client'

import React, {useContext, useEffect, useRef} from 'react'
import {useForm} from 'react-hook-form'
import {useDispatch} from 'react-redux'
import Link from 'next/link'
import {CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import {Label} from "@/components/ui/label"
import {Input} from "@/components/ui/input"
import {Button} from "@/components/ui/button"
import {Separator} from "@/components/ui/separator"
import {Icons} from "@/components/ui/icons"
import {useToast} from "@/hooks/use-toast"
import {LoginFormProps, OAuthProviderType} from "@/app/login/typing.d"
import FormStateContext from "@/app/login/context"
import {AppDispatch} from "@/stores"
import {CaptchaRef} from "@/components/Captcha/typing"
import {queryServer} from "@/app/login/server"
import {userLogin} from "@/api/authController";
import {setLoginUser} from "@/stores/LoginUser";
import EnhancedController from "@/components/EnhancedController";
import {useAppSelector} from "@/stores/hooks";
import {UserRoleEnum} from "@/enums/access";
import {FormStateEnum} from "@/app/login/enums";
import {useUserInfo} from "@/hooks/useUserInfo";
import {Captcha} from '@/components/Captcha'
import {RegexPattern} from "@/lib/regex";

const OAuthProvider: OAuthProviderType[] = [
	{
		name: "Google",
		icon: <Icons.google/>,
		url: "https://google.com",
	},
	{
		name: "Github",
		icon: <Icons.Github/>,
		url: "https://github.com"
	}
]

const OAuthButton = ({provider}: { provider: OAuthProviderType }) => (
	<Button variant="outline" className="w-full">
		<div style={{
			display: "flex",
			justifyContent: "space-between",
			alignItems: "center",
			width: "100%"
		}}>
			<div style={{
				width: "1.0rem",
				height: "1.0rem",
				marginRight: "0.5rem",
				justifyContent: "left"
			}}>
				{provider.icon}
			</div>
			<p style={{
				textAlign: "center",
				flex: 1 /* 让<p>占据多余的空间，确保文字在视觉上居中 */
			}}>{`使用 ${provider.name} 登录`}</p>
		</div>
	</Button>
);

interface FormValues {
	email: string
	password: string
}

const LoginFormPage: React.FC<LoginFormProps> = ({email, setEmail}) => {
	const {toast} = useToast()
	const {setFormState} = useContext(FormStateContext)
	const captchaRef = useRef<CaptchaRef>(null)
	const dispatch = useDispatch<AppDispatch>()
	const userInfo = useUserInfo()
	useEffect(() => {
		// 如果用户已经登录，则跳转到首页
		if (userInfo?.userRole === UserRoleEnum.IS_LOGIN) {
			window.location.href = "/"
		}
	}, []);

	const {control, register, handleSubmit, formState: {errors, isSubmitting}} = useForm<FormValues>({
		defaultValues: {
			email: email,
			password: ''
		}
	})

	const onSubmit = async (data: FormValues) =>
	{
		try {
			const result = await userLogin({
				userAccount: data.email,
				userPassword: data.password,
				captcha: captchaRef.current?.getValue(),
				captchaId: captchaRef.current?.getCaptchaId() as string
			})
			if (result?.code === 0) {
				toast({
					description: "登录成功",
					duration: 5000,
					position: "top-center"
				})
				dispatch(setLoginUser(result?.data))
				window.location.href = "/"
			} else {
				toast({
					description: result?.message || "登录失败",
					duration: 5000,
					position: "top-center"
				})
				captchaRef.current?.refreshCaptcha?.()
			}
		} catch (error) {
			toast({
				description: "登录失败，请稍后重试",
				duration: 5000,
				position: "top-center"
			})
			captchaRef.current?.refreshCaptcha?.()
		}
	}

	return (
		<>
			<CardHeader>
				<CardTitle className="text-2xl">登录</CardTitle>
				<CardDescription>
					输入你的账号进行登录
				</CardDescription>
			</CardHeader>

			<CardContent>
				<form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
					<div className="space-y-2">
						<Label htmlFor="email">邮箱</Label>
						<EnhancedController
							control={control}
							name="email"
							rules={{
								required: "邮箱是必填的",
								pattern: {
									value: RegexPattern.ACCOUNT_REGEX.pattern,
									message: RegexPattern.ACCOUNT_REGEX.message
								}
							}}
						>
							<Input placeholder="m@example.com"/>
						</EnhancedController>
					</div>
					<div className="space-y-2">
						<div className="flex items-center">
							<Label htmlFor="password">密码</Label>
							<Link href="#" className="ml-auto text-sm underline" onClick={() => setFormState(FormStateEnum.FORGET)}>
								已注册，忘记密码？
							</Link>
						</div>
						<EnhancedController
							control={control}
							name="password"
							rules={{
								required: "密码是必填的",
								pattern: {
									value: RegexPattern.PASSWORD_REGEX.pattern,
									message: RegexPattern.PASSWORD_REGEX.message
								}
							}}
						>
							<Input.Password
								id="password"
								placeholder="输入您的密码"
								{...register("password", {required: "密码是必填的"})}
							/>
						</EnhancedController>
					</div>
					<div className="space-y-2">
						<div className="flex items-center">
							<Label htmlFor="password">验证码</Label>
						</div>
						<Captcha.Image
							fetchImage={async () => await queryServer.captchaImage() as API.CaptchaVO}
							ref={captchaRef}
						/>
					</div>
					<Button type="submit" className="w-full" disabled={isSubmitting}>
						{isSubmitting ? "登录中..." : "登录"}
					</Button>
				</form>

				<Separator className="my-4"/>

				<div className="grid gap-2 w-full">
					{OAuthProvider.map((provider) => (
						<OAuthButton key={provider.name} provider={provider}/>
					))}
				</div>

				<div className="mt-4 text-center text-sm">
					没有账号?{" "}
					<a onClick={() => setFormState(FormStateEnum.REGISTER)} className="underline cursor-pointer">
						去注册
					</a>
				</div>
			</CardContent>
		</>
	)
}

export default LoginFormPage
