"use client"

import {
    Folder,
    MoreHorizontal,
    Share,
    Trash2,
    Plus,
} from "lucide-react"

import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    SidebarGroup,
    SidebarGroupLabel,
    SidebarMenu,
    SidebarMenuAction,
    SidebarMenuButton,
    SidebarMenuItem,
    useSidebar,
} from "@/components/ui/sidebar"
import {Button} from "@/components/ui/button";
import React, {useEffect, useState} from "react";
import useAsyncHandler from "@/hooks/useAsyncHandler";
import {getMyGroupItems} from "@/api/groupController";
import {Skeleton} from "@/components/ui/skeleton";
import {LoginUser} from "@/app/typing";
import {Condition, Conditional} from "@/components/Conditional";
import Link from "next/link";
import {useAppDispatch} from "@/stores/hooks";
import {onChangeGroupClick, setGroupItem} from "@/stores/Group";
import {GroupAddDialog} from "@/components/SiderBar/AddGroupFormModal";


// 封装组件
export function NavGroups({userInfo} : {
    userInfo: LoginUser
}) {
    const { isMobile } = useSidebar()
    const [queryGroupItemHandler, isLoading] = useAsyncHandler<API.GroupItemVO[]>();
    const [groupItems, setGroupItems] = useState<Array<API.GroupItemVO>>([])
    const dispatch = useAppDispatch();
    const queryGroupItems = async () => {
        const groupItemResult = await queryGroupItemHandler(async () => {
            const {data} = await getMyGroupItems();
            return data || [];
        }, [])
        setGroupItems(groupItemResult)
        dispatch(setGroupItem(groupItemResult));
    }
    useEffect(()=> {
        queryGroupItems();
    }, [])
    const handleGroupClick = (groupItem: API.GroupItemVO) => {
        dispatch(onChangeGroupClick(groupItem));
    }
    return (
        <SidebarGroup className="group-data-[collapsible=icon]:hidden">
            <div className="flex items-center justify-between">
                <SidebarGroupLabel className="text-base">分组管理</SidebarGroupLabel>
                <GroupAddDialog refresh={queryGroupItems}/>
            </div>
            <SidebarMenu>
                <Conditional>
                    <Condition.When test={isLoading}>
                        {Array(3)
                            .fill(0)
                            .map((_, index) => (
                                <SidebarMenuItem key={index}>
                                    <Skeleton className="h-8 w-full" />
                                </SidebarMenuItem>
                            ))}
                    </Condition.When>

                    {/* 没有分组 */}
                    <Condition.When test={groupItems.length === 0}>
                        <SidebarMenuItem className="flex h-[calc(80vh-200px)] w-full items-center justify-center">
                            <div className="flex flex-col items-center justify-center text-center">
                                <Folder className="mb-2 h-12 w-12 text-muted-foreground" />
                                <p className="mb-4 text-sm text-muted-foreground">暂无分组</p>
                                <Button
                                    variant="ghost"
                                    size="sm"
                                    // onClick={handleAddGroup}
                                    className="text-primary hover:bg-primary/10"
                                >
                                    <Plus className="mr-2 h-4 w-4" />
                                    <span className="text-base">创建新分组</span>
                                </Button>
                            </div>
                        </SidebarMenuItem>
                        {/* 显示分组列表 */}
                        <Condition.Else>
                            {groupItems.map((item) => (
                                <SidebarMenuItem key={item.name}>
                                    <SidebarMenuButton asChild>
                                        <Link
                                            href={`/link/${item.gid}`}
                                            onClick={() => handleGroupClick(item)}
                                        >
                                            <span
                                                className="flex h-6 w-6 items-center justify-center rounded-full bg-primary text-white text-sm font-medium">
                                                {item.linkCount}
                                            </span>
                                            <span>{item.name}</span>
                                        </Link>
                                    </SidebarMenuButton>
                                    <DropdownMenu>
                                        <DropdownMenuTrigger asChild>
                                            <SidebarMenuAction showOnHover>
                                            <MoreHorizontal />
                                                <span className="sr-only">More</span>
                                            </SidebarMenuAction>
                                        </DropdownMenuTrigger>
                                        <DropdownMenuContent
                                            className="w-48"
                                            side={isMobile ? "bottom" : "right"}
                                            align={isMobile ? "end" : "start"}
                                        >
                                            <DropdownMenuItem>
                                                <Share className="mr-2 h-4 w-4 text-muted-foreground" />
                                                <span>分享分组</span>
                                            </DropdownMenuItem>
                                            <DropdownMenuSeparator />
                                            <DropdownMenuItem>
                                                <Trash2 className="mr-2 h-4 w-4 text-muted-foreground" />
                                                <span>删除分组</span>
                                            </DropdownMenuItem>
                                        </DropdownMenuContent>
                                    </DropdownMenu>
                                </SidebarMenuItem>
                            ))}
                        </Condition.Else>
                    </Condition.When>
                </Conditional>
            </SidebarMenu>
        </SidebarGroup>
    )
}
