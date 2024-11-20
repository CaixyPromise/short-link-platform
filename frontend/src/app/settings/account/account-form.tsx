"use client"

import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {z} from "zod"

import {toast} from "@/hooks/use-toast"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {useUserInfo} from "@/hooks/useUserInfo";
import React, {useState} from "react";
import ModifyEmailModal from "@/app/settings/components/ModifyEmailModal";
import {Label} from "@/components/ui/label";
import ResetPasswordModal from "@/app/settings/components/ResetPasswordModal";

const accountFormSchema = z.object({
    email: z
        .string()
        .min(2, {
            message: "Name must be at least 2 characters.",
        })
        .max(30, {
            message: "Name must not be longer than 30 characters.",
        }),
    dob: z.date({
        required_error: "A date of birth is required.",
    }),
    language: z.string({
        required_error: "Please select a language.",
    }),
})

type AccountFormValues = z.infer<typeof accountFormSchema>


export function AccountForm()
{
    const userInfo = useUserInfo();
    const [modifyEmailVisible, setModifyEmailVisible] = useState<boolean>(false);
    const [resetPasswordVisible, setResetPasswordVisible] = useState<boolean>(false);

    return (
        <>
            <div className="space-y-2">
                <Label>邮箱</Label>
                <div className="flex items-center">
                    <Input
                        value={userInfo?.userEmail ?? ""}
                        disabled
                        className="text-sm text-muted-foreground"
                    />
                    <Button className="ml-2" onClick={() =>
                    {
                        setModifyEmailVisible(true)
                    }}>
                        修改邮箱
                    </Button>
                </div>
                <p className="text-[0.8rem] text-muted-foreground">
                    账号所绑定的邮箱，一个邮箱只能绑定一个账号。修改邮箱后需要重新登录。
                </p>
            </div>

            <div className="space-y-2">
                <Label>密码</Label>
                <div className="flex items-center">
                    <Label>
                        密码强度:
                        <span style={{color: "green"}}>强</span>
                    </Label>
                    <Button
                        className="ml-5"
                        onClick={() =>
                        {
                            setResetPasswordVisible(true)
                        }}>
                        修改密码
                    </Button>
                </div>
                <p className="text-[0.8rem] text-muted-foreground">
                    修改密码需要校验邮箱信息，同时需要重新登录。
                </p>
            </div>

            <ModifyEmailModal setVisible={setModifyEmailVisible} visible={modifyEmailVisible}/>
            <ResetPasswordModal visible={resetPasswordVisible} setVisible={setResetPasswordVisible} />
        </>
    )
}
