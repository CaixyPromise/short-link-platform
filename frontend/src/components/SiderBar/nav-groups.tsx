"use client"

import {
    Folder,
    MoreHorizontal,
    Share,
    Trash2,
    Plus, PenLine,
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
import React, {useCallback, useEffect, useState} from "react";
import useAsyncHandler from "@/hooks/useAsyncHandler";
import {getMyGroupItems} from "@/api/groupController";
import {Skeleton} from "@/components/ui/skeleton";
import {LoginUser} from "@/app/typing";
import {Condition, Conditional} from "@/components/Conditional";
import Link from "next/link";
import {useAppDispatch, useAppSelector} from "@/stores/hooks";
import {modifyGroupItem, onChangeGroupClick, setGroupItem} from "@/stores/Group";
import {GroupAddDialog} from "@/components/SiderBar/AddGroupFormModal";
import UpdateGroupForm from "@/components/SiderBar/UpdateGroupFormModal";


// 封装组件
export function NavGroups({userInfo} : {
    userInfo: LoginUser
}) {
    const { isMobile } = useSidebar();
    const groupStoreItems = useAppSelector((state) => state.Group.groupList);
    const [queryGroupItemHandler, isLoading] = useAsyncHandler<API.GroupItemVO[]>();
    const [updateGroupModalVisible, setUpdateGroupModalVisible] = useState(false);
    const [currentSelectedGroup, setCurrentSelectedGroup] = useState<API.GroupItemVO | null>(null);
    const dispatch = useAppDispatch();

    const queryGroupItems = useCallback(async () => {
        const groupItemResult = await queryGroupItemHandler(async () => {
            const { data } = await getMyGroupItems();
            return data || [];
        }, []);
        dispatch(setGroupItem(groupItemResult)); // 更新 Redux 状态
    }, [dispatch, queryGroupItemHandler]);


    useEffect(() => {
        queryGroupItems(); // 页面加载时获取分组数据
    }, [queryGroupItems]);
    const handleGroupClick = (groupItem: API.GroupItemVO) => {
        dispatch(onChangeGroupClick(groupItem));
    }
    const handleUpdateGroupClick = (groupItem: API.GroupItemVO) => {
        setCurrentSelectedGroup(groupItem);
        setUpdateGroupModalVisible(true);
    }
    const handleUpdateGroupClose = useCallback((newData?: API.GroupItemVO) => {
        setUpdateGroupModalVisible(false);
        setCurrentSelectedGroup(null);
        if (newData) {
            dispatch(modifyGroupItem(newData)); // 更新 Redux 状态中的分组数据
            // queryGroupItems(); // 重新查询分组数据
        }
    }, [dispatch, queryGroupItems]);

    return (
        <>
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
                        <Condition.When test={groupStoreItems.length === 0}>
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
                                {groupStoreItems.map((item) => (
                                    <SidebarMenuItem key={item.gid}>
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
                                                {/*<DropdownMenuItem>*/}
                                                {/*    <Share className="mr-2 h-4 w-4 text-muted-foreground" />*/}
                                                {/*    <span>分享分组</span>*/}
                                                {/*</DropdownMenuItem>*/}
                                                <DropdownMenuItem
                                                    onClick={() => handleUpdateGroupClick(item)}
                                                >
                                                    <PenLine className="mr-2 h-4 w-4 text-muted-foreground" />
                                                    <span>更新分组</span>
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
            <UpdateGroupForm
                isOpen={updateGroupModalVisible}
                onClose={handleUpdateGroupClose}
                gid={currentSelectedGroup?.gid}
            />
        </>
    )
}
