"use client"
import React, {useRef} from 'react';
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
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import {useUserInfo} from "@/hooks/useUserInfo";
import PasswordInput from "@/components/PasswordInput";
import {ResultCode} from "@/enums/ResultCodeEnum";
import {useRouter} from "next/navigation";
import {CodeCaptchaProps, CodeCaptchaRef} from "@/components/Captcha/captcha-code";

const schema = z.object({
	newPassword: z
		.string()
		.min(6, {message: "密码至少需要6个字符",}),
	confirmPassword: z
		.string()
		.min(6, {message: "密码至少需要6个字符",}),
});

type FormData = z.infer<typeof schema>;

export interface ModifyEmailModalProps {
	visible: boolean;
	setVisible: (visible: boolean) => void;
}

/**
 * 用在修改邮箱、修改密码的Modal
 *
 * @author CAIXYPROMISE
 * @version 1.0
 * @since 2024/10/14 下午10:57
 */
const ModifyEmailModal: React.FC<ModifyEmailModalProps> = ({visible, setVisible}) => {
	const {toast} = useToast();
	const [submitModifyHandler] = useAsyncHandler<boolean>();
	const captchaRef = useRef<CodeCaptchaRef>(null);
	const userInfo = useUserInfo();
	const router = useRouter();
	const form = useForm<FormData>({
		resolver: zodResolver(schema),
	});
	const {register, handleSubmit, formState: {errors}, setValue, getValues} = form;

	const fetchRestPasswordCode = () => {
		queryServer.modifyPasswordStepByIdentification()
			.then(({code, data})=>{
				if (code === ResultCode.SUCCESS) {
					toast({
						title: '验证码已发送',
						description: '请查收邮箱',
					});
				}
			})
			.catch((error: Error)=>{
				toast({
					title: '验证码发送失败',
					description: '请稍后再试',
					variant: 'destructive'
				});
			})
	};

	const onSubmit: SubmitHandler<FormData> = async (data) => {
		const code = captchaRef.current?.getValue();
		if (!code) {
			toast({
				title: '请先完成验证',
				description: '请先完成验证',
				variant: 'destructive'
			});
			return;
		}
		const result = await submitModifyHandler(queryServer.resetPassword, [{
			captchaCode: code,
			confirmPassword: data.confirmPassword,
			newPassword: data.newPassword,
		}])
		if (result) {
			toast({
				title: '修改成功',
				description: '请重新登录',
			});
			setVisible(false);
			captchaRef?.current?.clear?.();
			// next 重新刷新页面
			router.push("/auth");
		}
	};

	return (
		<Dialog open={visible} onOpenChange={setVisible}>
			<DialogContent className="sm:max-w-[450px]">
				<DialogHeader>
					<DialogTitle>修改密码</DialogTitle>
					<DialogDescription>
						修改密码需要校验邮箱
					</DialogDescription>
				</DialogHeader>
				<Form {...form}>
					<form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
						<div className="space-y-2">
							<Label>当前邮箱</Label>
							<div className="flex items-center">
								<Input
									defaultValue={userInfo?.userEmail ?? ""}
									className="text-sm text-muted-foreground"
									disabled
								/>
							</div>
							<p className="text-[0.8rem] text-muted-foreground">
								当前账号所绑定的密保邮箱，验证码会发到该邮箱内
							</p>
						</div>
						<div className="space-y-2">
							<Label>邮箱验证码</Label>
							<div className="flex items-center">
								<Captcha.Code
									ref={captchaRef}
									doSend={fetchRestPasswordCode}
									cooldownSeconds={60 * 5}
								/>
							</div>
						</div>
						<FormField
							control={form.control}
							name="newPassword"
							render={({field}) => (
								<FormItem>
									<FormLabel>新密码</FormLabel>
									<FormControl>
										<PasswordInput {...field} />
									</FormControl>
									<FormDescription>
										<span className="text-xs text-gray-500">密码长度在8-32位之间</span>
									</FormDescription>
									<FormMessage/>
								</FormItem>
							)}
						/>
						<FormField
							control={form.control}
							name="confirmPassword"
							render={({field}) => (
								<FormItem>
									<FormLabel>确认密码</FormLabel>
									<FormControl>
										<PasswordInput {...field} />
									</FormControl>
									<FormDescription>
										<span className="text-xs text-gray-500">再次输入新密码</span>
									</FormDescription>
									<FormMessage/>
								</FormItem>
							)}
						/>
						<DialogFooter>
							<Button type="submit">提交修改</Button>
						</DialogFooter>
					</form>
				</Form>
			</DialogContent>
		</Dialog>
	);
};

export default ModifyEmailModal;
