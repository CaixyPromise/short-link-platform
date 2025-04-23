'use client'
import React, {useRef, useState} from 'react'
import {useForm} from 'react-hook-form'
import {Eye, EyeOff} from 'lucide-react'
import {Input} from '@/components/ui/input'
import {Button} from '@/components/ui/button'
import {Label} from '@/components/ui/label'
import {Checkbox} from '@/components/ui/checkbox'
import {OAuthOptions} from "@/app/auth/components/oauth-options";
import {Captcha, CaptchaRef} from "@/components/Captcha";
import {userLogin} from "@/api/authController";
import {useToast} from "@/hooks/use-toast";
import {useRouter} from "next/navigation";
import {setLoginUser} from "@/stores/LoginUser";
import {useDispatch} from "react-redux";
import {AppDispatch} from "@/stores";
import {useAuthPageData} from "@/app/auth/contexts";
import {FormContainer} from "@/app/auth/components/form-container";
import {FormStateEnum} from "@/app/auth/enums";
import {queryServer} from "@/app/auth/server";
import {useAppSelector} from "@/stores/hooks";

interface LoginInputs {
	userAccount: string
	userPassword: string
}

export function LoginForm() {
	const [showPassword, setShowPassword] = useState(false)
	const {register, handleSubmit, formState: {errors}} = useForm<LoginInputs>()
	const captchaRef = useRef<CaptchaRef>(null)
	const dispatch = useDispatch<AppDispatch>()
	const {setAuthState} = useAuthPageData();
	const {title} = useAppSelector(state => state.SystemConfig)

	const {toast} = useToast()
	const router = useRouter()

	const onSubmit = async (formData: LoginInputs) => {
		try {
			const {code, data} = await userLogin({
				captcha: captchaRef.current?.getValue?.(),
				captchaId: captchaRef.current?.getCaptchaId?.() as string,
				...formData
			})
			if (code === 0) {
				toast({
					title: '登录成功',
					description: `欢迎回来 ${data?.userName || ''}`,
					duration: 3000
				})
				dispatch(setLoginUser(data))
				router.push('/')
			}
		} catch (e: Error) {
			toast({
				title: '登录失败',
				description: e.message,
				variant: 'destructive',
				position: 'top-center'
			})
		}
	}

	return (
		<FormContainer>
			<div className="mb-8 text-center">
				<div className="inline-block p-3 mb-4 rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600">
					<span className="text-2xl font-bold text-white text-white">AA</span>
				</div>
				<h2 className="text-2xl font-semibold text-gray-800">{title}-{FormStateEnum.LOGIN.getDescription()}</h2>
			</div>

			<form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
				<div className="space-y-2">
					<Label htmlFor="username" className="text-gray-700">
						邮箱
					</Label>
					<Input id="username" className="bg-white/50 border-gray-300" {...register("userAccount", {required: true})} />
				</div>

				<div className="space-y-2">
					<Label htmlFor="password" className="text-gray-700">
						密码
					</Label>
					<div className="relative">
						<Input
							id="password"
							type={showPassword ? "text" : "password"}
							className="bg-white/50 border-gray-300"
							{...register("userPassword", {required: true})}
						/>
						<button
							type="button"
							onClick={() => setShowPassword(!showPassword)}
							className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
						>
							{showPassword ? <EyeOff size={20}/> : <Eye size={20}/>}
						</button>
					</div>
				</div>

				<div className="space-y-2">
					<Label htmlFor="captcha">验证码</Label>
					<Captcha.Image
						inputClassName='bg-white/50 border-gray-300'
						fetchImage={async () => await queryServer.captchaImage() as API.CaptchaVO}
						ref={captchaRef}
					/>
				</div>

				<div className="flex items-center justify-between">
					<div className="flex items-center space-x-2">
						<Checkbox id="remember"/>
						<Label htmlFor="remember" className="text-gray-700">
							保持登录
						</Label>
					</div>
					<Button
						type='button'
						variant="link"
						className="text-indigo-600 hover:text-indigo-800"
						onClick={(e) => {
							e.preventDefault()
							setAuthState(FormStateEnum.FORGET)
						}}
					>
						忘记密码？
					</Button>
				</div>

				<Button type="submit" className="w-full bg-indigo-600 hover:bg-indigo-700 text-white">
					Login
				</Button>
			</form>

			<div className="mt-6 text-center text-sm text-gray-600">
				<span>没有账号？</span>
				<Button variant="link" onClick={() => setAuthState(FormStateEnum.REGISTER)}
				        className="text-indigo-600 hover:text-indigo-800">
					去注册
				</Button>
			</div>


			<OAuthOptions/>
		</FormContainer>
	)
}

