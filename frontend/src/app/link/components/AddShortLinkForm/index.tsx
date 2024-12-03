"use client"

import {Controller, useForm} from "react-hook-form"
import {zodResolver} from "@hookform/resolvers/zod"
import {Button} from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogDescription,
} from "@/components/ui/dialog"
import {
    Form,
    FormControl, FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"
import {Input} from "@/components/ui/input"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import {Textarea} from "@/components/ui/textarea"
import {formSchema, FormValues} from "./schema"
import DateTimeRangePicker from "@/components/DateTimeRangePicker";
import {addLink} from "@/api/linkController";
import {useToast} from "@/hooks/use-toast";
import {ValidDateEnum} from "@/enums/ValidDateEnum";
import {useAppSelector} from "@/stores/hooks";


interface AddShortLinkFormProps {
    open: boolean;
    setOpen: (open: boolean) => void;
    groupId: string;
}

export function AddShortLinkForm({open, setOpen, groupId}: AddShortLinkFormProps) {
    const {toast} = useToast();
    const currentGroup = useAppSelector(state => state.Group);

    const form = useForm<FormValues>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            createdType: 0,
            validDateType: 0,
        },
    })

    function onSubmit(data: FormValues) {
        const payload: API.LinkAddRequest= {
            ...data,
            gid: groupId,
        }
        console.log(data)
        if (payload.validDateType === ValidDateEnum.SPECIFIED) {
            payload.validDateStart = data.validDateRange?.fromDateTime;
            payload.validDateEnd = data.validDateRange?.toDateTime;
        }

        addLink(payload).then(() => {
            setOpen(false)
            toast({
                title: '创建成功',
                description: `创建短链接成功`,
                variant: 'default',
            })
        }).catch((error) => {
            toast({
                title: '创建失败',
                description: `创建短链接失败: ${error}`,
                variant: 'destructive',
            })
        })
    }

    return (
        <Dialog open={open} onOpenChange={setOpen} modal={false}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>创建短链接</DialogTitle>
                    <DialogDescription>
                        当前分组名称: {currentGroup.currentGroupName}
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                        <FormField
                            control={form.control}
                            name="originUrl"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>原始链接</FormLabel>
                                    <FormControl>
                                        <Input placeholder="https://example.com" {...field} />
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="linkName"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>短链接名称</FormLabel>
                                    <FormControl>
                                        <Input {...field} />
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <div className="space-y-4">
                            <FormField
                                control={form.control}
                                name="validDateType"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>有效期类型</FormLabel>
                                        <Select onValueChange={(value) => {
                                            field.onChange(parseInt(value));
                                            if (parseInt(value) === 0) {
                                                form.setValue('validDate', undefined);
                                            }
                                        }} defaultValue={field.value.toString()}>
                                            <FormControl>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="选择有效期类型"/>
                                                </SelectTrigger>
                                            </FormControl>
                                            <SelectContent>
                                                <SelectItem value="0">永久有效</SelectItem>
                                                <SelectItem value="1">自定义</SelectItem>
                                            </SelectContent>
                                        </Select>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />

                            {form.watch('validDateType') === 1 && (
                                <FormField
                                    control={form.control}
                                    name="validDateRange"
                                    render={({field}) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>有效期</FormLabel>
                                            <FormControl>
                                                <Controller
                                                    control={form.control}
                                                    name="validDateRange"
                                                    render={({field: controllerField}) => (
                                                        <DateTimeRangePicker
                                                            disablePast={true}
                                                            value={controllerField.value}
                                                            onChange={controllerField.onChange}
                                                        />
                                                    )}
                                                />
                                            </FormControl>
                                            <FormDescription>
                                                选择短链接的有效期。如果有效期类型为永久有效，则此字段可以忽略。
                                            </FormDescription>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                            )}
                        </div>
                        <FormField
                            control={form.control}
                            name="describe"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>描述</FormLabel>
                                    <FormControl>
                                        <Textarea placeholder="输入描述（可选）" {...field} />
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <Button type="submit">提交</Button>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    )
}

