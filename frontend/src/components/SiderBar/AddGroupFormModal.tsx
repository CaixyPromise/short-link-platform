"use client"

import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
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
import {Plus} from "lucide-react";
import {addGroup} from "@/api/groupController";
import {useToast} from "@/hooks/use-toast";
import {ToastAction} from "@/components/ui/toast";
import {useRouter} from "next/navigation";

const formSchema = z.object({
    groupName: z.string().min(1).max(16, "分组名称长度在1-16之间"),
    description: z.string().min(1).max(100, "分组描述长度在1-100之间").optional(),
    sortOrder: z.number().int().min(0).max(2147483646, "分组权重必须在0到2147483646之间"),
})

export function GroupAddDialog({refresh}: {
    refresh: () => Promise<void>
}) {
    const {toast} = useToast()
    const [open, setOpen] = useState(false)
    const [loading, setLoading] = useState(false)
    const router = useRouter()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            groupName: "",
            description: "",
            sortOrder: 0,
        },
    })

    async function onSubmit(values: z.infer<typeof formSchema>) {
        setOpen(false)
        addGroup({
            ...values
        }).then((res)=> {
            toast({
                title: "分组添加成功",
                description: `成功添加分组: ${values.groupName}`,
                action: <ToastAction altText="新增链接" onClick={()=>{
                    if (res.data && res?.data?.length > 0) {
                        router.push(`/link/${res.data}`)
                    }
                }}>新增链接</ToastAction>
            })
            refresh?.();
        }).catch((error) => {
            toast({
                title: "分组添加失败",
                description: `请稍后重试，原因: ${error}`,
                action: <ToastAction altText="重试" onClick={() => onSubmit(values)}>重试</ToastAction>,
            });
        })
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button
                    variant="ghost"
                    size="icon"
                    className="h-4 w-4"
                >
                    <Plus className="h-4 w-4"/>
                    <span className="sr-only">新增分组</span>
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>添加分组</DialogTitle>
                    <DialogDescription>
                        创建一个新的分组。请填写以下信息。
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                        <FormField
                            control={form.control}
                            name="groupName"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>分组名称</FormLabel>
                                    <FormControl>
                                        <Input placeholder="输入分组名称" {...field} />
                                    </FormControl>
                                    <FormDescription>
                                        分组名称长度在1-16之间
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="description"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>分组描述</FormLabel>
                                    <FormControl>
                                        <Textarea
                                            placeholder="输入分组描述"
                                            className="resize-none"
                                            {...field}
                                        />
                                    </FormControl>
                                    <FormDescription>
                                        分组描述长度在1-100之间
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="sortOrder"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>分组权重</FormLabel>
                                    <FormControl>
                                        <Input
                                            type="number"
                                            placeholder="输入分组权重"
                                            {...field}
                                            onChange={(e) => field.onChange(parseInt(e.target.value, 10))}
                                        />
                                    </FormControl>
                                    <FormDescription>
                                        分组权重必须在0到2147483646之间
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <DialogFooter>
                            <Button type="submit">提交</Button>
                        </DialogFooter>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    )
}

