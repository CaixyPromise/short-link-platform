"use client"

import {useEffect, useMemo, useState} from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import * as z from "zod";
import Spinner from "@/components/Spinner";
import {getGroupVoById, updateGroupByGid} from "@/api/groupController";
import {useToast} from "@/hooks/use-toast";

const groupUpdateSchema = z.object({
    name: z
        .string()
        .min(1, "名字是必须的")
        .max(16, "名称不超过16个字"),
    description: z
        .string()
        .min(1)
        .max(100, "分组描述不得超过100个字")
        .nullable(),
    sortOrder: z
        .number()
        .int()
        .min(0)
        .max(Number.MAX_VALUE - 1, "排序规则过大"),
});

type GroupUpdateFormValues = z.infer<typeof groupUpdateSchema>;

interface GroupUpdateDialogProps {
    isOpen: boolean;
    onClose: (newData?: API.GroupItemVO) => void;
    gid: string | null
}

export default function UpdateGroupForm({
    isOpen,
    onClose,
    gid,
}: GroupUpdateDialogProps) {
    const {toast} = useToast();
    const [loading, setLoading] = useState<boolean>(false);
    const form = useForm<GroupUpdateFormValues>({
        resolver: zodResolver(groupUpdateSchema),
    });

    const fetchGroupInfo = async () => {
        if (!gid)
            return;
        setLoading(true);
        getGroupVoById({
            gid
        })
            .then((res) => {
                form.reset({ ...res.data})
            })
            .catch((err) => {
                toast({
                    title: "获取分组信息失败",
                    description: err.message,
                    variant: "destructive",
                })
                onClose?.();
            })
            .finally(() => {
                setLoading(false);
            })
    }

    useEffect(() => {
        if (isOpen) {
            fetchGroupInfo()
        }
    }, [isOpen]);

    const handleSubmit = (data: GroupUpdateFormValues) => {
        setLoading(true)
        updateGroupByGid({
            gid: gid,
            ...data
        }).then((res)=> {
            if (res.code === 0) {
                toast({
                    title: "更新成功",
                    description: "分组信息已更新",
                })
            }
            onClose({
                ...data,
                gid
            } as API.GroupItemVO)
        }).catch((error) => {
            toast({
                title: "更新失败",
                description: error.message,
                variant: "destructive",
            })
        }).finally(() => {
            setLoading(false)
        })
    };

    return (
        <Dialog open={isOpen} onOpenChange={(state) => {
            console.log("onOpenChange：", state);
            return !state && onClose()
        }} modal={false}>
            <DialogContent
                           onInteractOutside={(event) => {
                               event.preventDefault();
                           }}
            >
                <Spinner loading={loading} loadingText="正在加载中">
                    <DialogHeader>
                        <DialogTitle>更新分组</DialogTitle>
                    </DialogHeader>
                    <DialogDescription>更新分组信息</DialogDescription>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Name</FormLabel>
                                        <FormControl>
                                            <Input {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="description"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Description</FormLabel>
                                        <FormControl>
                                            <Textarea {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="sortOrder"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Sort Order</FormLabel>
                                        <FormControl>
                                            <Input
                                                type="number"
                                                {...field}
                                                onChange={(e) => field.onChange(parseInt(e.target.value, 10))}
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <Button type="submit">Update</Button>
                        </form>
                    </Form>
                </Spinner>
            </DialogContent>
        </Dialog>
    );
}