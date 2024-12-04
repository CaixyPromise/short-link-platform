"use client"

import React, { useCallback, useEffect, useState } from "react"
import { DndProvider } from 'react-dnd'
import { HTML5Backend } from 'react-dnd-html5-backend'
import { Folder, Plus } from 'lucide-react'
import { SidebarGroup, SidebarGroupLabel, SidebarMenu, SidebarMenuItem, useSidebar } from "@/components/ui/sidebar"
import { Button } from "@/components/ui/button"
import { Skeleton } from "@/components/ui/skeleton"
import { Condition, Conditional } from "@/components/Conditional"
import { useAppDispatch, useAppSelector } from "@/stores/hooks"
import {
    deleteGroupItem,
    modifyGroupItem,
    onChangeGroupClick,
    setGroupItem,
    updateGroupLinkCountByGid
} from "@/stores/Group"
import { GroupAddDialog } from "@/components/SiderBar/AddGroupFormModal"
import UpdateGroupForm from "@/components/SiderBar/UpdateGroupFormModal"
import { DraggableMenuItem } from "./DraggableMenuItem"
import useAsyncHandler from "@/hooks/useAsyncHandler"
import {deleteGroup, getMyGroupItems, updateGroupByGid, updateGroupOrder} from "@/api/groupController"
import useDebounce from "@/hooks/useDebounce";
import {useToast} from "@/hooks/use-toast";
import ConfirmationModal from "@/components/confirmation-modal";
import {ConfirmationModalProvider} from "@/components/confirmation-modal/ConfirmationModalContext";
import DeleteGroupFormModal from "@/components/SiderBar/DeleteGroupFormModal";

export function NavGroups() {
    const { isMobile } = useSidebar()
    const {toast} = useToast()
    const groupStoreItems = useAppSelector((state) => state.Group.groupList)
    const [queryGroupItemHandler, isLoading] = useAsyncHandler<API.GroupItemVO[]>()
    const [updateGroupModalVisible, setUpdateGroupModalVisible] = useState(false)
    const [currentSelectedGroup, setCurrentSelectedGroup] = useState<API.GroupItemVO>({})
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false)

    const dispatch = useAppDispatch()

    const queryGroupItems = useCallback(async () => {
        const groupItemResult = await queryGroupItemHandler(async () => {
            const { data } = await getMyGroupItems()
            return data || []
        }, [])
        dispatch(setGroupItem(groupItemResult))
    }, [dispatch, queryGroupItemHandler])

    useEffect(() => {
        queryGroupItems()
    }, [queryGroupItems])
    
    const groupUpdateRequest = useCallback(async (groupItem: API.GroupItemVO | undefined) => {
        if (!groupItem) {
            return Promise.reject();
        }
        const { data, code, message } = await updateGroupByGid(groupItem)
        if (code === 0) {
            toast({
                title: "更新成功",
                description: "分组信息已更新",
            })
            return Promise.resolve();
        } else {
            toast({
                title: "更新失败",
                description: message,
                variant: "destructive",
            })
            return Promise.reject();
        }
    }, [toast])

    const handleGroupClick = (groupItem: API.GroupItemVO) => {
        dispatch(onChangeGroupClick(groupItem))
    }

    const handleUpdateGroupClick = (groupItem: API.GroupItemVO) => {
        setCurrentSelectedGroup(groupItem)
        setUpdateGroupModalVisible(true)
    }

    const handleUpdateGroupClose = useCallback((newData?: API.GroupItemVO) => {
        setUpdateGroupModalVisible(false)
        setCurrentSelectedGroup(null)
        if (newData) {
            dispatch(modifyGroupItem(newData))
        }
    }, [dispatch])

    const moveItem = useCallback(
        (dragIndex: number, hoverIndex: number) => {
            const dragItem = groupStoreItems[dragIndex];
            const newItems = [...groupStoreItems];
            newItems.splice(dragIndex, 1);
            newItems.splice(hoverIndex, 0, dragItem);

            const updatedItems = newItems.map((item, index) => ({
                ...item,
                sortOrder: index,
            }));
            dispatch(setGroupItem(updatedItems));
        },
        [groupStoreItems, dispatch]
    );

    const dropItem = useCallback(
        async (gid: string, offset: number) => {
            console.log("call dropItem: ", gid, offset)
            if (gid.length === 0) {
                return
            }
            updateGroupOrder({
                gid,
                offset
            }).then(()=> {
                toast({
                    title: "分组顺序更新成功",
                    description: "分组顺序已更新",
                })
            }).catch((reason)=>{
                toast({
                    title: "分组顺序更新失败",
                    description: reason.message,
                    variant: "destructive",                   
                })
            })
        },
        [toast]
    );

    const handleDeleteGroup = (gid: string, moveTo?: string) =>
    {
        if (!gid || gid.length === 0) {
            return
        }
        deleteGroup({
            gid,
            newGroupId: moveTo
        }).then((res)=>{
            toast({
                title: "删除成功",
                description: "分组已成功删除",
            })
            dispatch(deleteGroupItem(gid))
            console.log("res: ", res)
            if (moveTo) {
                dispatch(updateGroupLinkCountByGid({
                    gid: moveTo,
                    linkCount: res.data as number
                }))
            }
        }).catch((reason)=>{
            toast({
                title: "删除失败",
                description: reason.message,
                variant: "destructive"
            })
        }).finally(()=>{
            setIsDeleteModalOpen(false)
        })
    }

    const handleDeleteOpen = (groupItem: API.GroupItemVO) => {
        if (!groupItem) {
            return;
        }
        setCurrentSelectedGroup(groupItem)
        setIsDeleteModalOpen(true)
    }

    const [debouncedMoveItem] = useDebounce(moveItem, 300);

    return (
        <ConfirmationModalProvider>
            <DndProvider backend={HTML5Backend}>
                <SidebarGroup className="group-data-[collapsible=icon]:hidden">
                    <div className="flex items-center justify-between">
                        <SidebarGroupLabel className="text-base">分组管理</SidebarGroupLabel>
                        <GroupAddDialog refresh={queryGroupItems} />
                    </div>
                    <SidebarMenu className="space-y-0.5 transition-all duration-200">
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

                            <Condition.When test={groupStoreItems.length === 0}>
                                <SidebarMenuItem className="flex h-[calc(80vh-200px)] w-full items-center justify-center">
                                    <div className="flex flex-col items-center justify-center text-center">
                                        <Folder className="mb-2 h-12 w-12 text-muted-foreground" />
                                        <p className="mb-4 text-sm text-muted-foreground">暂无分组</p>
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            className="text-primary hover:bg-primary/10"
                                        >
                                            <Plus className="mr-2 h-4 w-4" />
                                            <span className="text-base">创建新分组</span>
                                        </Button>
                                    </div>
                                </SidebarMenuItem>
                                <Condition.Else>
                                    {groupStoreItems.map((item, index) => (
                                        <DraggableMenuItem
                                            key={item.gid}
                                            item={item}
                                            index={index}
                                            dropItem={dropItem}
                                            moveItem={debouncedMoveItem}
                                            handleGroupClick={handleGroupClick}
                                            handleUpdateGroupClick={handleUpdateGroupClick}
                                            handleDeleteGroupClick={handleDeleteOpen}
                                            isMobile={isMobile}
                                        />
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
                    submitter={groupUpdateRequest}
                />
                <DeleteGroupFormModal
                    onOpenChange={()=>setIsDeleteModalOpen(false)}
                    onSubmit={handleDeleteGroup}
                    gid={currentSelectedGroup?.gid}
                    visible={isDeleteModalOpen}
                />
            </DndProvider>
        </ConfirmationModalProvider>
    )
}

