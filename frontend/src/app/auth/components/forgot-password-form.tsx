'use client'

import {useForm} from 'react-hook-form'
import {Input} from '@/components/ui/input'
import {Button} from '@/components/ui/button'
import {Label} from '@/components/ui/label'
import {OAuthOptions} from "@/app/auth/components/oauth-options";
import {useAuthPageData} from "@/app/auth/contexts";
import {FormContainer} from "@/app/auth/components/form-container";
import {FormStateEnum} from "@/app/auth/enums";
import {useAppSelector} from "@/stores/hooks";

interface ForgotPasswordInputs {
	email: string
}

export function ForgotPasswordForm() {
	const {register, handleSubmit, formState: {errors}} = useForm<ForgotPasswordInputs>()
	const {setAuthState} = useAuthPageData();
	const {title} = useAppSelector(state => state.SystemConfig)

	const onSubmit = (data: ForgotPasswordInputs) => {
		console.log(data)
	}

	return (
		<FormContainer>
			<div className="mb-8 text-center">
				<div className="inline-block p-3 mb-4 rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600">
					<span className="text-2xl font-bold text-white">AA</span>
				</div>
				<h2 className="text-2xl font-semibold text-gray-800">{title}{FormStateEnum.FORGET.getDescription()}</h2>
			</div>

			<form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
				<div className="space-y-2">
					<Label htmlFor="email">注册邮箱</Label>
					<Input
						id="email"
						type="email"
						className="bg-white/50 border-gray-300"
						{...register('email', {required: true})}
					/>
				</div>

				<Button type="submit" className="w-full bg-indigo-600 hover:bg-indigo-700 text-white">
					发送重置密码邮件
				</Button>
			</form>

			<div className="mt-6 text-center text-sm text-gray-600">
				<Button variant="link" onClick={() =>{setAuthState(FormStateEnum.LOGIN)}} className="text-indigo-600 hover:text-indigo-800">
					去登录
				</Button>
			</div>

			<OAuthOptions/>

			<div className="mt-8 text-center">
				<h2 className="text-2xl font-bold text-gray-800">Join Our Community</h2>
				<p className="text-sm text-gray-600">Create Your High-Level Cloud Network Service!</p>
			</div>
		</FormContainer>
	)
}

