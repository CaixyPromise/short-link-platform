import React, { useRef } from "react";
import { useDrag, useDrop, DropTargetMonitor, DragSourceMonitor } from "react-dnd";
import { SidebarMenuItem, SidebarMenuButton, SidebarMenuAction } from "@/components/ui/sidebar";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import {MoreHorizontal, PenLine, Trash2, GripVertical, Info} from "lucide-react";
import Link from "next/link";
import { motion } from "framer-motion";
import {Identifier} from "dnd-core";
import {useConfirmationModal} from "@/components/confirmation-modal/ConfirmationModalContext";
import {deleteGroup} from "@/api/groupController";
import {useToast} from "@/hooks/use-toast";

const ItemTypes = {
    MENU_ITEM: "menuItem",
};

type DraggableItem = {
    id: string;
    index: number;
    originalIndex: number
};

type DraggableMenuItemProps = {
    item: API.GroupItemVO
    index: number;
    moveItem: (dragIndex: number, hoverIndex: number) => void;
    dropItem: (gid: string, offset: number) => Promise<void>;
    handleGroupClick: (item: API.GroupItemVO) => void;
    handleUpdateGroupClick: (item: API.GroupItemVO) => void;
    handleDeleteGroupClick: (groupItem: API.GroupItemVO) => void
    isMobile: boolean;
};

export function DraggableMenuItem({
    item,
    index,
    moveItem,
    handleGroupClick,
    handleUpdateGroupClick,
    isMobile,
    dropItem,
    handleDeleteGroupClick
}: DraggableMenuItemProps) {
    const ref = useRef<HTMLDivElement | null>(null);
    const [{ handlerId }, drop] = useDrop<
        DraggableItem,
        void,
        { handlerId: Identifier | null }
    >({
        accept: ItemTypes.MENU_ITEM,
        collect: (monitor) => ({
            handlerId: monitor.getHandlerId(),
        }),
        hover(draggedItem: DraggableItem, monitor: DropTargetMonitor) {
            if (!ref.current) return;

            const dragIndex = draggedItem.index;
            const hoverIndex = index;

            if (dragIndex === hoverIndex) return;

            moveItem(dragIndex, hoverIndex);
        },
        drop(draggedItem: DraggableItem, monitor: DropTargetMonitor) {
            if (!ref.current) return;
            const offset = index - draggedItem.originalIndex;
            dropItem(draggedItem.id, offset);
        }
    });


    const [{ isDragging }, drag, preview] = useDrag<
        DraggableItem,
        void,
        { isDragging: boolean }
    >({
        type: ItemTypes.MENU_ITEM,
        item: () => ({ id: item.gid as string, index, originalIndex: index}),
        collect: (monitor: DragSourceMonitor) => ({
            isDragging: monitor.isDragging(),
        }),
    });

    const setPreviewRef = (node: HTMLDivElement | null) => {
        ref.current = node;
        preview(node);
    };

    drag(drop(ref));

    return (
        <motion.div
            ref={setPreviewRef}
            style={{
                opacity: isDragging ? 0.5 : 1,
                cursor: "move",
            }}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.2 }}
            layout
            layoutId={item.gid}
        >
            <SidebarMenuItem data-handler-id={handlerId}>
                <div className="flex items-center w-full">
                    <GripVertical className="mr-2 h-4 w-4 text-muted-foreground cursor-move" />
                    <SidebarMenuButton asChild>
                        <Link href={`/link/${item.gid}`} onClick={() => handleGroupClick(item)}>
              <span className="flex h-6 w-6 items-center justify-center rounded-full bg-primary text-white text-sm font-medium">
                {item.linkCount}
              </span>
                            <span>{item.name}</span>
                        </Link>
                    </SidebarMenuButton>
                </div>
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
                        <DropdownMenuItem onClick={() => handleUpdateGroupClick(item)}>
                            <PenLine className="mr-2 h-4 w-4 text-muted-foreground" />
                            <span>更新分组</span>
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem onClick={() => {
                            handleDeleteGroupClick(item)
                        }}>
                            <Trash2 className="mr-2 h-4 w-4 text-muted-foreground" />
                            <span>删除分组</span>
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </SidebarMenuItem>
        </motion.div>
    );
}
