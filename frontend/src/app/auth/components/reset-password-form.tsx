"use client"

import React, {useCallback, useEffect, useState} from "react"
import {useForm} from "react-hook-form"
import {Input} from "@/components/ui/input"
import {Button} from "@/components/ui/button"
import {Label} from "@/components/ui/label"
import {AnimatePresence, motion} from "framer-motion"
import {CheckCircle, Eye, EyeOff, XCircle} from "lucide-react"
import {useAuthPageData} from "@/app/auth/contexts";
import {OAuthOptions} from "@/app/auth/components/oauth-options";
import {InputOTP, InputOTPGroup, InputOTPSeparator, InputOTPSlot} from "@/components/ui/input-otp";
import {FormContainer} from "@/app/auth/components/form-container";
import {useToast} from "@/hooks/use-toast";
import {doActivateUser, getRegistrationInfoByParams} from "@/api/authController";
import {FormStateEnum} from "@/app/auth/enums";
import {useAppSelector} from "@/stores/hooks";
import {ResultCode} from "@/enums/ResultCodeEnum";

interface ActivateAccountInputs {
	password: string;
	confirmPassword: string;
}

export function ActivateAccountForm() {
	const {
		register,
		handleSubmit,
		formState: { errors },
	} = useForm<ActivateAccountInputs>()
	const {title} = useAppSelector(state => state.SystemConfig)
	const [submitStatus, setSubmitStatus] = useState<"idle" | "success" | "error">("idle")
	const [showPassword, setShowPassword] = useState(false)
	const [codeInput, setCodeInput] = useState<string>("");
	const [userInfo, setUserInfo] = useState<API.RegistrationInfo>({});
	const [activateInfo, setActivateInfo] = useState<{
		token?: string;
		code?: string;
	}>({});

	const {toast} = useToast();
	const {setAuthState, authState, searchParams} = useAuthPageData();

	const doActivate = async (formData: ActivateAccountInputs) => {
		const {token} = activateInfo
		if (!token || !codeInput) {
			toast({
				title: "验证码错误：获取用户信息失败，请检查激活链接是否正确或已提交注册",
			});
			return;
		}
		try {
			const {code, data} = await doActivateUser({
				token,
				code: codeInput
			}, {
				...formData
			})
			if (code === ResultCode.SUCCESS && data === true) {
				toast({
					title: "激活成功",
					description: "请登录",
					action: <>
						<Button variant="link" onClick={() =>{setAuthState(FormStateEnum.LOGIN)}} className="text-indigo-600 hover:text-indigo-800">
							去登录
						</Button>
					</>
				})
				setSubmitStatus("success")
			}
		}
		catch (error: Error) {
			toast({
				title: "激活失败",
				description: `${error.message}`,
				variant: 'destructive'
			})
			setSubmitStatus("error")
		}
	}

	const queryTokenUserInfo = useCallback(async (token:string) => {
		try {
			const {code, data} = await getRegistrationInfoByParams({
				token
			});
			if (code === ResultCode.SUCCESS) {
				setUserInfo(data);
			}
		} catch (error: Error) {
			toast({
				title: "验证码错误：获取用户信息失败，请检查激活链接是否正确或已提交注册",
			});
			// 清理网址路径请求参数
			window.history.replaceState(null, "", "/auth");
			setAuthState(FormStateEnum.REGISTER)
		}
	},[setAuthState, toast])

	useEffect(() => {
		if (authState === FormStateEnum.ACTIVATE) {
			const token = searchParams?.get("token");
			const code = searchParams?.get("code");
			if (!token || !code) {
				setAuthState(FormStateEnum.REGISTER)
				toast({
					title: "验证码错误：获取用户信息失败，请检查激活链接是否正确或已提交注册",
				});
			}
			queryTokenUserInfo(token as string)
			setCodeInput(code as string)
			setActivateInfo({
				token: token as string,
				code: code as string,
			})
		}
	}, [authState, queryTokenUserInfo, searchParams, setAuthState, toast]);

	return (
		<FormContainer>
			<div className="mb-8 text-center">
				<div className="inline-block p-3 mb-4 rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600">
					<span className="text-2xl font-bold text-white">AA</span>
				</div>
				<h2 className="text-2xl font-semibold text-gray-800">{title}-{FormStateEnum.ACTIVATE.getDescription()}</h2>
			</div>

			<form onSubmit={handleSubmit(doActivate)} className="space-y-6">
				<div className="space-y-2">
					<Label htmlFor="email">邮箱：{userInfo?.email}</Label> <br/>
					<Label htmlFor="nickName">用户名：{userInfo?.nickName}</Label>
				</div>

				<div className='space-y-2'>
					<Label htmlFor="code">激活码</Label>
					<InputOTP
						className="w-full"
						maxLength={6}
						value={codeInput}
						onChange={(value) => setCodeInput(value)}
					>
						<InputOTPGroup className="flex flex-1">
							<InputOTPSlot index={0} className="flex-1" />
							<InputOTPSlot index={1} className="flex-1" />
							<InputOTPSlot index={2} className="flex-1" />
						</InputOTPGroup>

						<InputOTPSeparator />

						<InputOTPGroup className="flex flex-1">
							<InputOTPSlot index={3} className="flex-1" />
							<InputOTPSlot index={4} className="flex-1" />
							<InputOTPSlot index={5} className="flex-1" />
						</InputOTPGroup>
					</InputOTP>

				</div>

				<div className="space-y-2">
					<Label htmlFor="password">输入密码</Label>
					<div className="relative">
						<Input
							id="password"
							type={showPassword ? 'text' : 'password'}
							className="bg-white/50 border-gray-300"
							{...register('password', {required: true})}
						/>
						<button
							type="button"
							onClick={() => setShowPassword(!showPassword)}
							className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
						>
							{showPassword ? <EyeOff size={20}/> : <Eye size={20}/>}
						</button>
					</div>
				</div>

				<div className="space-y-2">
					<Label htmlFor="confirmPassword">确认密码</Label>
					<div className="relative">
						<Input
							id="confirmPassword"
							type={showPassword ? 'text' : 'password'}
							className="bg-white/50 border-gray-300"
							{...register('confirmPassword', {required: true})}
						/>
					</div>
				</div>

				<Button type="submit" className="w-full bg-indigo-600 hover:bg-indigo-700 text-white">
					激活账号
				</Button>
			</form>

			<AnimatePresence>
				{submitStatus !== "idle" && (
					<motion.div
						initial={{opacity: 0, y: 20}}
						animate={{opacity: 1, y: 0}}
						exit={{opacity: 0, y: -20}}
						transition={{duration: 0.3}}
						className={`mt-4 p-3 rounded-md ${submitStatus === "success" ? "bg-green-500/20" : "bg-red-500/20"}`}
					>
						{submitStatus === "success" ? (
							<div className="flex items-center">
								<CheckCircle className="w-5 h-5 mr-2 text-green-500"/>
								<span>激活成功</span>
							</div>
						) : (
							<div className="flex items-center">
								<XCircle className="w-5 h-5 mr-2 text-red-500"/>
								<span>激活失败</span>
							</div>
						)}
					</motion.div>
				)}
			</AnimatePresence>

			{submitStatus === "success" && (
				<motion.div
					initial={{opacity: 0, y: 20}}
					animate={{opacity: 1, y: 0}}
					transition={{duration: 0.3, delay: 0.3}}
					className="mt-4"
				>
					<Button onClick={() => setAuthState(FormStateEnum.LOGIN)} className="w-full bg-indigo-600 hover:bg-indigo-700 text-white">
						去登录
					</Button>
				</motion.div>
			)}

			<div className="mt-6 text-center text-sm text-gray-600">
				<span>已有账号?</span>
				<Button variant="link" onClick={() => setAuthState(FormStateEnum.LOGIN)} className="text-indigo-600 hover:text-indigo-800">
					去登录
				</Button>
				<div>
					<span>没有注册？</span>
					<Button variant="link" onClick={() => setAuthState(FormStateEnum.REGISTER)}
					        className="text-indigo-600 hover:text-indigo-800">
						去注册
					</Button>
				</div>
			</div>
			<OAuthOptions/>
		</FormContainer>
	)
}

