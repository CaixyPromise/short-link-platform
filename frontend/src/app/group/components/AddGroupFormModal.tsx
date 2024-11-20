import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle} from "@/components/ui/dialog";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import React from "react";
import useAsyncHandler from "@/hooks/useAsyncHandler";
import {SubmitHandler, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {addGroup} from "@/api/groupController";
import {useToast} from "@/hooks/use-toast";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import NumberInput from "@/components/NumberInput";
import {Textarea} from "@/components/ui/textarea";

interface AddGroupFormModalProps {
    addGroupModalVisible: boolean;
    setAddGroupModalVisible: (visible: boolean) => void;
}

const schema = z.object({
    groupName: z
        .string()
        .max(6, { message: "分组名称长度在1-16之间" })
        .min(1, { message: "分组名称长度在1-16之间" }),
    sortOrder: z
        .number()
        .max(Number.MAX_VALUE - 1, { message: "分组排序权重不能大于最大值" })
        .min(0, { message: "分组排序权重不能小于0" }),
    description: z
        .string()
        .max(100, { message: "分组描述长度在0-100之间" })
        .min(0, { message: "分组描述长度在0-100之间" }),
});

type FormData = z.infer<typeof schema>;

export default function AddGroupFormModal({
                                              addGroupModalVisible,
                                              setAddGroupModalVisible
                                          }: AddGroupFormModalProps): React.JSX.Element {
    const {toast} = useToast();
    const [submitHandler, loading] = useAsyncHandler();
    const form = useForm<FormData>({
        resolver: zodResolver(schema),
    });
    const { register, handleSubmit, formState: { errors }, setValue, getValues } = form;

    const handleAddGroupSubmit: SubmitHandler<FormData> = async (data) => {
        // 如果表单校验失败，不会进入这里
        try {
            // 提交成功后执行逻辑
            const { data: responseData, code } = await addGroup({
                ...data
            });
            if (code === 0) {
                toast({
                    title: "添加分组成功",
                    description: "分组已成功添加",
                });
                setAddGroupModalVisible(false); // 提交成功后关闭模态框
            } else {
                toast({
                    title: "添加分组失败",
                    description: responseData?.message || "失败，请重试。",
                    variant: "destructive",
                });
            }
        } catch (error) {
            toast({
                title: "出错了",
                description: "提交过程中发生了错误，请稍后再试。",
                variant: "destructive",
            });
        }
    };

    return (
        <Dialog open={addGroupModalVisible} onOpenChange={setAddGroupModalVisible}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>添加分组</DialogTitle>
                </DialogHeader>
                <Form {...form}>
                    <form onSubmit={handleSubmit(handleAddGroupSubmit)} className="space-y-4">
                        <FormField
                            control={form.control}
                            name="groupName"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>分组名称</FormLabel>
                                    <FormControl>
                                        <Input placeholder="分组名称长度在1-16之间" {...field} />
                                    </FormControl>
                                    <FormMessage>
                                        {errors.groupName && <span>{errors.groupName.message}</span>}
                                    </FormMessage>
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="description"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>分组描述</FormLabel>
                                    <FormControl>
                                        <Textarea placeholder="分组描述不超过100字" {...field}/>
                                    </FormControl>
                                    <FormMessage>
                                        {errors.description && <span>{errors.description.message}</span>}
                                    </FormMessage>
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="sortOrder"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>分组排序权重</FormLabel>
                                    <FormDescription>分组排序权重不能小于0</FormDescription>
                                    <FormControl>
                                        <NumberInput {...field} />
                                    </FormControl>
                                    <FormMessage>
                                        {errors.sortOrder && <span>{errors.sortOrder.message}</span>}
                                    </FormMessage>
                                </FormItem>
                            )}
                        />
                        <DialogFooter>
                            <Button variant="outline" onClick={() => setAddGroupModalVisible(false)}>取消</Button>
                            <Button type="submit">确定</Button>
                        </DialogFooter>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}
