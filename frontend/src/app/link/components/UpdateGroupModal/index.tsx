import { useAppSelector } from "@/stores/hooks";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandInput,
    CommandItem,
    CommandList,
} from "@/components/ui/command";
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "@/components/ui/popover";
import { Check, ChevronsUpDown } from "lucide-react";
import { useState, useEffect } from "react";
import { updateLinkGroup } from "@/api/linkController";
import { useToast } from "@/hooks/use-toast";
import { ToastAction } from "@/components/ui/toast";
import {useRouter} from "next/navigation";

interface UpdateGroupModalProps {
    linkId?: number;
    open: boolean;
    onOpenChange: (state: boolean) => void;
    originGroupId?: string;
}

export default function UpdateGroupModal({
                                             linkId,
                                             open,
                                             onOpenChange,
                                             originGroupId,
                                         }: UpdateGroupModalProps) {
    const groupStore = useAppSelector((state) => state.Group);
    const { currentGroupId, groupList, currentGroupName } = groupStore;
    const { toast } = useToast();
    const router = useRouter();

    const updateGroupSchema = z.object({
        newGroupId: z.string(),
        newGroupName: z.string(),
    });

    const groupOptions = groupList
        ? groupList
            .filter((item) => item.gid !== currentGroupId)
            .map((item) => ({ value: item.gid, label: item.name }))
        : [];

    const initialGroupId = originGroupId || currentGroupId;
    const initialGroupName =
        groupOptions.find((groupItem) => groupItem.value === initialGroupId)
            ?.label || currentGroupName || "未知分组";

    const form = useForm<z.infer<typeof updateGroupSchema>>({
        resolver: zodResolver(updateGroupSchema),
        defaultValues: {
            newGroupId: initialGroupId,
            newGroupName: initialGroupName,
        },
    });

    useEffect(() => {
        if (originGroupId || currentGroupId) {
            form.reset({
                newGroupId: initialGroupId,
                newGroupName: initialGroupName,
            });
        }
    }, [originGroupId, currentGroupId, form, initialGroupId, initialGroupName]);

    const [popoverOpen, setPopoverOpen] = useState(false);

    function onSubmit(values: z.infer<typeof updateGroupSchema>) {
        updateLinkGroup({
            newGroupId: values.newGroupId,
            groupId: originGroupId,
            linkIds: [linkId],
        }).then(() => {
            toast({
                title: "修改成功",
                description: `链接分组修改成功，新的分组名称为：${values.newGroupName}`,
                action: <ToastAction altText="to" onClick={()=>{
                    router.push(`/link/${values.newGroupId}`)
                }}>去查看</ToastAction>,
            });
            onOpenChange(false);
        });
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange} modal={false}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>修改链接分组</DialogTitle>
                    <DialogDescription>
                        当前分组名称：{currentGroupName}
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form
                        onSubmit={form.handleSubmit(onSubmit)}
                        className="space-y-6"
                    >
                        <FormField
                            control={form.control}
                            name="newGroupId"
                            render={({ field }) => (
                                <FormItem className="flex flex-col">
                                    <FormLabel>分组名称</FormLabel>
                                    <Popover
                                        open={popoverOpen}
                                        onOpenChange={setPopoverOpen}
                                    >
                                        <PopoverTrigger asChild>
                                            <FormControl>
                                                <Button
                                                    variant="outline"
                                                    role="combobox"
                                                    onClick={() =>
                                                        setPopoverOpen(true)
                                                    }
                                                    className={cn(
                                                        "w-full justify-between",
                                                        !field.value &&
                                                        "text-muted-foreground"
                                                    )}
                                                >
                                                    {form.getValues(
                                                        "newGroupName"
                                                    ) || "选择分组"}
                                                    <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                </Button>
                                            </FormControl>
                                        </PopoverTrigger>
                                        <PopoverContent className="w-[var(--radix-popper-anchor-width)] p-0">
                                            <Command>
                                                <CommandInput placeholder="搜索分组..." />
                                                <CommandList>
                                                    <CommandEmpty>
                                                        未找到分组。
                                                    </CommandEmpty>
                                                    <CommandGroup>
                                                        {groupOptions.map(
                                                            (groupItem) => (
                                                                <CommandItem
                                                                    value={
                                                                        groupItem.label
                                                                    }
                                                                    key={
                                                                        groupItem.value
                                                                    }
                                                                    onSelect={() => {
                                                                        form.setValue(
                                                                            "newGroupId",
                                                                            groupItem.value
                                                                        );
                                                                        form.setValue(
                                                                            "newGroupName",
                                                                            groupItem.label
                                                                        );
                                                                        setPopoverOpen(
                                                                            false
                                                                        ); // 选择后关闭 Popover
                                                                    }}
                                                                >
                                                                    <Check
                                                                        className={cn(
                                                                            "mr-2 h-4 w-4",
                                                                            groupItem.value ===
                                                                            field.value
                                                                                ? "opacity-100"
                                                                                : "opacity-0"
                                                                        )}
                                                                    />
                                                                    {
                                                                        groupItem.label
                                                                    }
                                                                </CommandItem>
                                                            )
                                                        )}
                                                    </CommandGroup>
                                                </CommandList>
                                            </Command>
                                        </PopoverContent>
                                    </Popover>
                                    <FormDescription>
                                        选择需要切换的分组
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <Button type="submit">提交修改</Button>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}
