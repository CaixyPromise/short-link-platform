import {EmptyIcon} from "@/components/Empty/empty-icon";
import {AlertCircle} from "lucide-react";
import {EmptyDescription} from "@/components/Empty/empty-description";
import {Empty} from "@/components/Empty/empty";
import React from "react";

const DefaultTableEmpty = () => {
    return (
        <Empty>
            <EmptyIcon>
                <AlertCircle className="text-blue-500 w-20 h-20 text-muted-foreground" />
            </EmptyIcon>
            <EmptyDescription>
                <span>没有数据:(</span>
            </EmptyDescription>
        </Empty>
    )
}

export default DefaultTableEmpty;