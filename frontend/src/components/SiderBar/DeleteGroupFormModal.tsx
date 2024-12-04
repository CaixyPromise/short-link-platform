"use client";

import {useAppSelector} from "@/stores/hooks";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import {AlertTriangle} from "lucide-react";
import React from "react";
import * as z from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/components/ui/button";
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";

interface DeleteGroupFormModalProps {
    onSubmit: (gid: string, moveTo?: string) => void; // 接受迁移目标 gid
    onOpenChange: () => void;
    gid: string;
    visible: boolean;
}

const deleteGroupSchema = z.object({
    moveTo: z.string().optional(), // 新分组的 gid
});

function DeleteGroupFormModal({
                                  onSubmit,
                                  onOpenChange,
                                  gid,
                                  visible,
                              }: DeleteGroupFormModalProps) {
    const groupList = useAppSelector((state) => state.Group);
    const deleteGroup = groupList.groupList.find((group) => group.gid === gid);
    const requiredMove = (deleteGroup?.linkCount ?? 0) > 0;
    const deleteForm = useForm<z.infer<typeof deleteGroupSchema>>({
        resolver: zodResolver(deleteGroupSchema),
    });

    const handleOnSubmit = (data: z.infer<typeof deleteGroupSchema>) =>
    {
        if (requiredMove && !data.moveTo) {
            deleteForm.setError("moveTo", {
                type: "manual",
                message: "请选择迁移到的分组",
            });
            return; // 阻止表单提交
        }
        onSubmit(deleteGroup?.gid,requiredMove ? data?.moveTo : null); // 如果没有迁移，则传递 null
    };

    return (
        <Dialog open={visible} onOpenChange={onOpenChange} modal={false}>
            <DialogContent
                onInteractOutside={(event) => {
                    event.preventDefault();
                }}
            >
                <DialogHeader>
                    <DialogTitle>
                        <div className="flex items-center space-x-1">
                            <AlertTriangle className="h-6 w-6 text-yellow-500" />
                            <h2>删除分组</h2>
                        </div>
                    </DialogTitle>
                </DialogHeader>
                <DialogDescription>
                    <p className="text-sm">
                        你确定要删除分组
                        <span className="text-yellow-500">{deleteGroup?.name}</span> 吗？
                        <strong>此操作不可逆。</strong>
                    </p>
                </DialogDescription>
                <Form {...deleteForm}>
                    <form onSubmit={deleteForm.handleSubmit(handleOnSubmit)} className="space-y-4">
                        {requiredMove && (
                            <FormField
                                control={deleteForm.control}
                                name="moveTo"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>请选择分组内的短链移动到的新分组</FormLabel>
                                        <FormControl>
                                            <Select
                                                onValueChange={field.onChange}
                                                value={field.value}
                                                defaultValue=""
                                            >
                                                <SelectTrigger>
                                                    <SelectValue placeholder="选择新的分组" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {groupList.groupList
                                                        .filter((group) => group.gid !== gid)
                                                        .map((group) => (
                                                            <SelectItem key={group.gid} value={group.gid}>
                                                                {group.name}
                                                            </SelectItem>
                                                        ))}
                                                </SelectContent>
                                            </Select>
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        )}
                        <Button type="submit">确认删除</Button>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}

export default DeleteGroupFormModal;
