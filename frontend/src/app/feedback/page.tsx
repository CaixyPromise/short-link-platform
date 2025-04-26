"use client"

import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import * as z from "zod"
import { motion } from "framer-motion"
import { Loader2 } from 'lucide-react'

import { Button } from "@/components/ui/button"
import {
	Form,
	FormControl,
	FormDescription,
	FormField,
	FormItem,
	FormLabel,
	FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import {useToast} from "@/hooks/use-toast";
import {postFeedback} from "@/api/feedbackController";
import {useDialog} from "@/components/Result/contexts/ModalContext";
import {ResultCode} from "@/enums/ResultCodeEnum";

const formSchema = z.object({
	contactName: z.string().min(2, "名字至少需要2个字符").max(50, "名字不能超过50个字符"),
	contactEmail: z.string().email("请输入有效的邮箱地址"),
	title: z.string().min(1, "标题不能为空").max(30, "标题不能超过30个字符"),
	content: z.string().min(1, "反馈内容不能为空").max(512, "反馈内容不能超过512个字符"),
})

export default function FeedbackForm() {
	const [isSubmitting, setIsSubmitting] = useState(false)
	const {openDialog} = useDialog();
	const {toast } = useToast()
	const form = useForm<z.infer<typeof formSchema>>({
		resolver: zodResolver(formSchema),
		defaultValues: {
			contactName: "",
			contactEmail: "",
			title: "",
			content: "",
		},
		mode: "onChange", // 添加这一行
	})

	async function onSubmit(values: z.infer<typeof formSchema>) {
		setIsSubmitting(true)
		postFeedback({
			...values
		}).then((res)=>{
			const {code, data} = res;
			const isSucceed = code === ResultCode.SUCCESS
			if (isSucceed) {
				form.reset()
				openDialog({
					status: "success",
					title: "提交成功" ,
					subText: "感谢您的反馈，我们会尽快处理",
					modal: false
				})
			}
		}).catch((error: Error) => {
			toast({
				title: "提交失败",
				description: error.message,
				variant: "destructive",
			})
		}).finally(()=> {
			setIsSubmitting(false)
		})

	}

	return (
		<Form {...form}>
			<form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
				<motion.div
					initial={{ opacity: 0, y: 20 }}
					animate={{ opacity: 1, y: 0 }}
					transition={{ duration: 0.5 }}
				>
					<FormField
						control={form.control}
						name="contactName"
						render={({ field }) => (
							<FormItem>
								<FormLabel>姓名</FormLabel>
								<FormControl>
									<Input placeholder="请输入您的姓名" {...field} />
								</FormControl>
								<FormMessage />
							</FormItem>
						)}
					/>
				</motion.div>

				<motion.div
					initial={{ opacity: 0, y: 20 }}
					animate={{ opacity: 1, y: 0 }}
					transition={{ duration: 0.5, delay: 0.1 }}
				>
					<FormField
						control={form.control}
						name="contactEmail"
						render={({ field }) => (
							<FormItem>
								<FormLabel>邮箱</FormLabel>
								<FormControl>
									<Input placeholder="请输入您的邮箱" {...field} />
								</FormControl>
								<FormMessage />
							</FormItem>
						)}
					/>
				</motion.div>

				<motion.div
					initial={{ opacity: 0, y: 20 }}
					animate={{ opacity: 1, y: 0 }}
					transition={{ duration: 0.5, delay: 0.2 }}
				>
					<FormField
						control={form.control}
						name="title"
						render={({ field }) => (
							<FormItem>
								<FormLabel>标题</FormLabel>
								<FormControl>
									<Input placeholder="请输入反馈标题" {...field} />
								</FormControl>
								<FormDescription>
									标题长度不能超过30个字符
								</FormDescription>
								<FormMessage />
							</FormItem>
						)}
					/>
				</motion.div>

				<motion.div
					initial={{ opacity: 0, y: 20 }}
					animate={{ opacity: 1, y: 0 }}
					transition={{ duration: 0.5, delay: 0.3 }}
				>
					<FormField
						control={form.control}
						name="content"
						render={({ field }) => (
							<FormItem>
								<FormLabel>反馈内容</FormLabel>
								<FormControl>
									<Textarea
										placeholder="请输入您的反馈内容"
										className="resize-y h-32"
										onInput={(e) => {
											const target = e.target as HTMLTextAreaElement;
											target.style.height = "auto"; // 重置高度
											target.style.height = `${target.scrollHeight}px`; // 根据内容设置高度
										}}
										{...field}
									/>

								</FormControl>
								<div className="flex justify-between items-center">
									<FormDescription>
										反馈内容不能超过512个字符
									</FormDescription>
									<span className={`text-sm ${
										field.value.length >= 500 ? 'text-red-500' : 'text-gray-500'
									}`}>
                    {field.value.length} / 512
                  </span>
								</div>
								<FormMessage />
							</FormItem>
						)}
					/>
				</motion.div>

				<motion.div
					initial={{ opacity: 0, y: 20 }}
					animate={{ opacity: 1, y: 0 }}
					transition={{ duration: 0.5, delay: 0.4 }}
				>
					<Button type="submit" disabled={isSubmitting}>
						{isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
						提交反馈
					</Button>
				</motion.div>
			</form>
		</Form>
	)
}

