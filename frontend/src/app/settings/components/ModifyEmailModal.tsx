"use client"
import React, {useCallback, useRef, useState} from 'react';
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
} from "@/components/ui/dialog";
import {Label} from "@/components/ui/label";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Captcha, CaptchaRef} from '@/components/Captcha';
import {queryServer} from "@/app/settings/server";
import useAsyncHandler from "@/hooks/useAsyncHandler";
import {EmailBizEnum} from "@/enums/EmailBizEnum";
import {useToast} from "@/hooks/use-toast";
import {useForm, SubmitHandler} from "react-hook-form";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import PasswordInput from "@/components/PasswordInput";
import {Form, FormItem, useFormInstance} from "@/components/Form";
import {useUserInfo} from "@/hooks/useUserInfo";
import {ResultCode} from "@/enums/ResultCodeEnum";
import {defineStepper} from "@/components/stepper";
import {ModifyEmailContext, useModifyEmailContext} from "@/app/settings/context/ModifyEmailContext";
import {Condition, Conditional} from "@/components/Conditional";
import Result from "@/components/Result";
import {useRouter} from "next/navigation";

const checkUserStatusSchema = z.object({
	code: z
		.string()
		.min(1, '验证码是必须的')
		.max(6, "验证码错误"),
	password: z
		.string()
		.min(8, '密码需要在8-20位之间，大小写和特殊字符')
		.max(20, '密码需要在8-20位之间，大小写和特殊字符')
		.optional()
	,
});

const checkNewEmailSchema = z.object({
	newEmail: z.string()
		.email({message: "无效的邮箱地址"}),
	code: z
		.string()
		.min(1, '验证码是必须的')
		.max(6, "验证码错误"),
});

type CheckUserStatusValues = z.infer<typeof checkUserStatusSchema>;
type CheckNewEmailValues = z.infer<typeof checkNewEmailSchema>;

export interface ModifyEmailModalProps {
	visible: boolean;
	setVisible: (visible: boolean) => void;
}

const {useStepper, utils} = defineStepper(
	{
		id: 'identify',
		title: '校验身份',
		description: '输入以下校验信息以确定您是本人操作',
	},
	{
		id: 'update',
		title: '更新邮箱',
		description: '按照操作校验和更新最新邮箱，完成后将使用新邮箱进行登录',
	}, {
		id: 'success',
		title: '操作成功',
		description: '邮箱更新成功，请使用新邮箱进行登录',
	}
);


const StepIndicator = ({
	                       currentStep,
	                       totalSteps,
	                       size = 80,
	                       strokeWidth = 6,
                       }: StepIndicatorProps) => {
	const radius = (size - strokeWidth) / 2;
	const circumference = radius * 2 * Math.PI;
	const fillPercentage = (currentStep / totalSteps) * 100;
	const dashOffset = circumference - (circumference * fillPercentage) / 100;

	return (
		<div className="relative inline-flex items-center justify-center">
			<svg width={size} height={size}>
				<title>Step Indicator</title>
				<circle
					cx={size / 2}
					cy={size / 2}
					r={radius}
					fill="none"
					stroke="currentColor"
					strokeWidth={strokeWidth}
					className="text-muted-foreground"
				/>
				<circle
					cx={size / 2}
					cy={size / 2}
					r={radius}
					fill="none"
					stroke="currentColor"
					strokeWidth={strokeWidth}
					strokeDasharray={circumference}
					strokeDashoffset={dashOffset}
					className="text-primary transition-all duration-300 ease-in-out"
					transform={`rotate(-90 ${size / 2} ${size / 2})`}
				/>
			</svg>
			<div className="absolute inset-0 flex items-center justify-center">
        <span className="text-sm font-medium" aria-live="polite">
          {currentStep} / {totalSteps}
        </span>
			</div>
		</div>
	);
};

const CheckUserStatus = () => {
	const userInfo = useUserInfo();
	const [currentEmail, setCurrentEmail] = useState<string>('');
	const [showPasswordInput, setShowPasswordInput] = useState<boolean>(false);
	const {toast} = useToast();
	const {setFormData, stepper} = useModifyEmailContext();

	const fetchRestEmailCode = useCallback((_: string) => {
		if (!currentEmail || currentEmail?.length === 0) {
			toast({
				title: '请输入邮箱',
				description: '请输入邮箱以发送验证码',
				variant: 'destructive'
			});
			return false;
		}
		return queryServer.submitModifyEmailStepByCheckOrigin({
			originalEmail: currentEmail
		}).then((response) => {
			if (response?.code === ResultCode.SUCCESS) {
				toast({
					title: '验证码已发送',
					description: '请查收原邮箱',
				})
				setShowPasswordInput(true)
			}
			return true
		}).catch((e) => {
			toast({
				title: '验证码发送失败',
				description: e?.message,
				variant: 'destructive'
			})
			return false
		});
	}, [currentEmail, toast])

	const submitModifyStepByPasswordAndCode = useCallback((data: CheckUserStatusValues) => {
		// console.log("data", data);
		return queryServer.submitModifyStepByPasswordAndCode({
			...data
		}).then((response) => {
			const {code, data} = response;
			if (code === ResultCode.SUCCESS) {
				setFormData?.(prevState => ({
					...prevState,
					token: data as string
				}))
				stepper?.next();
			}
		}).catch((error: Error) => {
			toast({
				title: '验证失败',
				description: error?.message,
				variant: 'destructive'
			})
		});
	}, [setFormData, stepper])

	return (
		<div className='space-y-4 text-start'>
			<Label>
				当前邮箱: {userInfo?.userEmail}
			</Label>

			<div className='space-y-2'>
				<Label htmlFor='currentEmail'>请输入当前邮箱</Label>
				<Input
					id='currentEmail'
					placeholder="请输入当前邮箱以发送邮箱验证码"
					value={currentEmail}
					onChange={(e) => {
						e.preventDefault();
						setCurrentEmail(e.target?.value)
					}}
				/>
			</div>

			<Form<CheckUserStatusValues>
				resolver={zodResolver(checkUserStatusSchema)}
				onFinish={submitModifyStepByPasswordAndCode}
			>
				<Form.Item
					name="code"
					label="输入当前邮箱验证码"
					valuePropName='value'
					description='请输入当前邮箱收到的邮箱验证码'
				>
					<Captcha.Code doSend={fetchRestEmailCode}/>
				</Form.Item>

				<Form.Item
					name='password'
					label='输入用户密码'
					visible={showPasswordInput}
				>
					<PasswordInput placeholder='请输入当前用户密码'/>
				</Form.Item>
				<Conditional>
					<Condition.When test={showPasswordInput}>
						<div className='flex justify-end'>
							<Button type='submit'>
								下一步
							</Button>
						</div>
					</Condition.When>
				</Conditional>
			</Form>
		</div>
	)
}

const UpdateEmailForm = () => {
	const [form] = Form.useForm<CheckNewEmailValues>();
	const {setFormData, setVisible, formData, stepper} = useModifyEmailContext();
	const {toast} = useToast();
	const isInvalid = !formData || !formData.token;

	const token = formData?.token ?? ""; // 提前设个兜底字符串
	const fetchUpdateEmailCode = useCallback((_: string) => {
		return queryServer.validNewEmail({
			newEmail: form.getFieldValue('newEmail') as string,
			token: formData?.token
		}).then((response) => {
			const {code} = response;
			if (code === ResultCode.SUCCESS) {
				toast({
					title: '验证码已发送',
					description: `请查收新邮箱${form.getFieldValue('newEmail')}`,
				})
				return true;
			}
			return false;
		}).catch((error: Error) => {
			toast({
				title: '验证码发送失败',
				description: error.message,
				variant: 'destructive'
			})
			return false;
		})
	}, [formData?.token, toast]);

	const submitModifyEmail = useCallback((data: API.UserResetEmailRequest) => {
		return queryServer.modifyEmail({
			token,
			code: data?.code
		}).then((response) => {
			const {code} = response;
			if (code === ResultCode.SUCCESS) {
				stepper?.next();
				toast({
					title: '修改成功',
					description: '请使用新邮箱登录'
				})
			}
		}).catch((error: Error) => {
			toast({
				title: '修改失败',
				description: error.message,
				variant: 'destructive'
			})
		})
	}, [stepper, toast, token])

	if (isInvalid) {
		return <Result title='非法操作' status='failure' subText='缺少身份校验信息'>
			<div className='flex items-center gap-4'>
				<Button variant='ghost' onClick={() => stepper?.prev()}>返回</Button>
				<Button variant='destructive' onClick={()=>setVisible?.(false)} >关闭</Button>
			</div>
		</Result>
	}


	return <>
		<div>
			<Form<CheckNewEmailValues>
				form={form}
				resolver={zodResolver(checkNewEmailSchema)}
				onFinish={submitModifyEmail}
			>
				<Form.Item
					name='newEmail'
					label='请输入新邮箱'
				>
					<Input
						placeholder='请输入新邮箱'
					/>
				</Form.Item>

				<Form.Item
					name='code'
					label='请输入新邮箱验证码'
				>
					<Captcha.Code doSend={fetchUpdateEmailCode}/>
				</Form.Item>
				<div className='flex justify-end'>
					<Button type='submit'>
						修改
					</Button>
				</div>
			</Form>
		</div>
	</>
}

const ModifiedSucceed = () => {
	const router = useRouter();
	return (
		<Result title='修改成功' status='success' subText='请使用新邮箱登录'>

			<Button variant='ghost' onClick={() => {
				router.push('/auth')
			}}>
				重新登录
			</Button>
		</Result>
	)
}

/**
 * 用在修改邮箱、修改密码的Modal
 *
 * @author CAIXYPROMISE
 * @version 1.0
 * @since 2024/10/14 下午10:57
 */
const ModifyEmailModal: React.FC<ModifyEmailModalProps> = ({visible, setVisible}) => {
	const stepper = useStepper();
	const [formData, setFormData] = useState<API.UserResetEmailRequest>({});

	const currentIndex = utils.getIndex(stepper.current.id);

	return (
		<Dialog open={visible} onOpenChange={setVisible}>
			<DialogContent className="sm:max-w-[550px]">
				<DialogHeader>
					<DialogTitle>修改邮箱</DialogTitle>
					<DialogDescription>
						修改邮箱需要校验新邮箱是否可用以及确认账号密码。
					</DialogDescription>
				</DialogHeader>
				<ModifyEmailContext.Provider value={{formData, setFormData, stepper, setVisible}}>
					<div className="flex items-center gap-4">
						<StepIndicator
							currentStep={currentIndex + 1}
							totalSteps={stepper.all.length}
						/>
						<div className="flex flex-col">
							<h2 className="flex-1 text-lg font-medium">
								{stepper.current.title}
							</h2>
							<p className="text-sm text-muted-foreground">
								{stepper.current.description}
							</p>
						</div>
					</div>

					{stepper.switch({
						identify: () => <CheckUserStatus/>,
						update: () => <UpdateEmailForm/>,
						success: () =>  <ModifiedSucceed/>
					})}
				</ModifyEmailContext.Provider>
			</DialogContent>
		</Dialog>
	);
};

export default ModifyEmailModal;
