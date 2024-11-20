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
import { useForm, SubmitHandler } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";

const schema = z.object({
    newEmail: z
        .string()
        .email({ message: "无效的邮箱地址" }),
    newEmailCaptcha: z
        .string()
        .optional(),
    userPassword: z
        .string()
        .min(6, { message: "密码至少需要6个字符", })
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
const ModifyEmailModal: React.FC<ModifyEmailModalProps> = ({ visible, setVisible }) => {
    const { toast } = useToast();
    const [fetchEmailHandler] = useAsyncHandler<boolean>();
    const [submitModifyHandler] = useAsyncHandler<boolean>();
    const captchaRef = useRef<CaptchaRef>(null);
    const form = useForm<FormData>({
        resolver: zodResolver(schema),
    });
    const { register, handleSubmit, formState: { errors }, setValue, getValues } = form;

    const emailSchema = z.string().email({ message: "无效的邮箱地址" });

    const fetchRestEmailCode = async () => {
        const email = getValues("newEmail"); // 获取当前表单中的邮箱值
        const result = emailSchema.safeParse(email);

        if (!result.success) {
            toast({
                title: '无效的邮箱地址',
                description: result.error.issues[0].message,
                variant: 'destructive'
            });
            return false; // 由于邮箱验证失败，停止执行后续代码
        }
        const fetchResult = await fetchEmailHandler(queryServer.fetchEmailCode, [{
            scenes: EmailBizEnum.MODIFY_EMAIL,
            toEmail: email
        }]);

        if (fetchResult) {
            toast({
                title: '验证码已发送',
                description: '请查收邮箱',
            });
        } else {
            toast({
                title: '验证码发送失败',
                description: '请稍后再试',
                variant: 'destructive'
            });
        }
    };

    const onSubmit: SubmitHandler<FormData> = async (data) => {
        const code = captchaRef.current?.getValue();
        console.log(code)
        if (!code) {
            toast({
                title: '请先完成验证',
                description: '请先完成验证',
                variant: 'destructive'
            });
            return;
        }
        const result = await submitModifyHandler(queryServer.modifyEmail, [{
            code: code,
            password: data.userPassword,
        }])
        if (result) {
            toast({
                title: '修改成功',
                description: '请重新登录',
            });
            setVisible(false);
            // next 重新刷新页面
            location.reload();
        }
    };

    return (
        <Dialog open={visible} onOpenChange={setVisible}>
            <DialogContent className="sm:max-w-[450px]">
                <DialogHeader>
                    <DialogTitle>修改邮箱</DialogTitle>
                    <DialogDescription>
                        修改邮箱需要校验新邮箱是否可用以及确认账号密码。
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                        <FormField
                            control={form.control}
                            name="newEmail"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>新邮箱</FormLabel>
                                    <FormControl>
                                        <Input {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="newEmailCaptcha"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>新邮箱验证码</FormLabel>
                                    <Captcha.Code
                                        ref={captchaRef}
                                        doSend={fetchRestEmailCode}
                                        cooldownSeconds={60 * 5}
                                    />
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="userPassword"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>用户密码</FormLabel>
                                    <FormControl>
                                        <Input.Password {...field} />
                                    </FormControl>
                                    <FormMessage />
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
