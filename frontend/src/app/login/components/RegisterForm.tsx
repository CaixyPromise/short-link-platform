"use client"
import React, {useContext, useEffect} from 'react';
import {CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import Link from "next/link";
import {Button} from "@/components/ui/button";
import Image from "next/image";
import {useToast} from "@/hooks/use-toast";
import {queryServer} from "@/app/login/server";
import Spinner from "@/components/Spinner";
import useAsyncHandler from "@/hooks/useAsyncHandler";
import {useUserInfo} from "@/hooks/useUserInfo";
import {UserRoleEnum} from "@/enums/access";
import {FormStateEnum} from "@/app/login/enums";
import FormStateContext from "@/app/login/context";


const RegisterFormPage: React.FC = () =>
{
    const {toast, customToast} = useToast();
    const { setFormState } = useContext(FormStateContext)

    const [queryCaptcha, isPending] = useAsyncHandler<API.CaptchaVO | null>()
    const [captchaImage, setCaptchaImage] = React.useState<string>("");
    const userInfo = useUserInfo()
    useEffect(() =>
    {
        console.log("userInfo: ", userInfo)
        // 如果用户已经登录，则跳转到首页
        if (userInfo?.userRole !== UserRoleEnum.NO_LOGIN) {
            window.location.href = "/"
        }
    }, []);
    const fetchImageCaptcha = async () =>
    {
        const payload = await queryCaptcha(async () =>
        {
            const data = await queryServer.captchaImage()
            console.log(data)
            if (!data)
            {
                toast({
                    title: "获取验证码失败",
                    description: "请稍后重试",
                    position: "top-center",
                    duration: 3000
                })
            }
            return data;
        }, [])
        if (payload)
        {
            setCaptchaImage(payload?.codeImage || "")
        }
    }


    useEffect(() =>
    {
        fetchImageCaptcha()
    }, [])
    return (
        <>
            <CardHeader>
                <CardTitle className="text-2xl">注册</CardTitle>
                <CardDescription>
                    输入邮箱进行注册
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div className="grid gap-4">
                    <div className="grid gap-2">
                        <Label htmlFor="email">邮箱</Label>
                        <Input
                            id="register-email"
                            type="email"
                            placeholder="example@example.com"
                            required
                        />
                    </div>
                    <div className="grid gap-2">
                        <div className="flex items-center">
                            <Label htmlFor="password">密码</Label>
                        </div>
                        <Input
                            id="register-password"
                            type="password"
                            placeholder="请输入密码"
                            required
                        />
                    </div>

                    <div className="grid gap-2">
                        <div className="flex items-center">
                            <Label htmlFor="confirmPsw">确认密码</Label>
                        </div>
                        <Input
                            id="password"
                            type="password"
                            placeholder="请确认密码"
                            required/>
                    </div>

                    <div className="grid gap-2">
                        <div className="flex items-center">
                            <Label htmlFor="captcha">验证码</Label>
                        </div>
                        <div className="flex items-center justify-between gap-2">
                            <Input id="captcha" type="text" required
                                   placeholder="请输入验证码"
                                   style={{
                                       width: "180px"
                                   }}
                            />
                            <Spinner loading={isPending}>
                                <Image
                                    src={"data:image/png;base64," + captchaImage} alt={"captcha"}
                                    width={130}
                                    height={100}
                                    style={{
                                        borderRadius: "5px"
                                    }}
                                    onClick={fetchImageCaptcha}
                                />
                            </Spinner>
                        </div>
                    </div>

                    <Button type="submit" className="w-full">
                        Register
                    </Button>

                </div>

                <div className="mt-4 text-center text-sm">
                    已经有账号？
                    <Link href="#" onClick={() => setFormState(FormStateEnum.LOGIN)} className="underline">
                        登录
                    </Link>
                </div>
            </CardContent>
        </>
    )
}

export default RegisterFormPage;
