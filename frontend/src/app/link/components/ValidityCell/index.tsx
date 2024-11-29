"use client"

import React from "react"
import {
    Dialog,
    DialogContent, DialogDescription,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import {ValidityPeriodForm} from "./validity-period-form"
import {useEffect} from "react";
import {AlertCircle} from "lucide-react";
import {ValidDateEnum} from "@/enums/ValidDateEnum";
import {updateLinkValidDate} from "@/api/linkController";
import {useToast} from "@/hooks/use-toast";

interface ValidityPeriodCellProps {
    record: {
        id: string;
        validDateType: number;
        validDateStart?: string;
        validDateEnd?: string;
        gid: string;
    } | null; // 支持 record 为空
    open: boolean; // 外部传入的状态
    onClose: () => void; // 关闭弹窗的回调
}

export function ValidityPeriodCell({record, open, onClose}: ValidityPeriodCellProps) {
    const {toast} = useToast()

    async function onUpdate(id: string, data: any): Promise<void> {
        const { validDateType, validDateRange } = data

        const validDateStart =
            validDateType === ValidDateEnum.SPECIFIED && validDateRange
                ? validDateRange.fromDateTime
                : null

        const validDateEnd =
            validDateType === ValidDateEnum.SPECIFIED && validDateRange
                ? validDateRange.toDateTime
                : null

        const payload:API.LinkUpdateValidDateRequest = {
            validDateType,
            validDateStart,
            validDateEnd,
            groupId: record?.gid,
            linkId: id
        }
        updateLinkValidDate(payload)
            .then((res) => {
                if (res.code === 0) {
                    toast({
                        title: "更新成功",
                        variant: "default"
                    })
                    onClose()
                }
            })
            .catch(() => {
                toast({
                    title: "更新失败",
                })
        })

    }


    return (
        <Dialog open={open} onOpenChange={(isOpen) => !isOpen && onClose()} modal={false}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>编辑有效期</DialogTitle>
                </DialogHeader>
                <DialogDescription>
                    设置短链有效期。
                </DialogDescription>
                {
                    !record ? (
                        <div className="flex flex-col items-center justify-center h-32">
                            <AlertCircle className="w-12 h-12"/>
                        </div>
                    ) : (
                        <ValidityPeriodForm
                            defaultValues={{
                                validDateType: record.validDateType,
                                validDateStart: record.validDateStart,
                                validDateEnd: record.validDateEnd,
                            }}
                            onSubmit={async (data) => {
                                const hasChanged =
                                    data.validDateType !== record.validDateType ||
                                    data.validDateStart !== record.validDateStart ||
                                    data.validDateEnd !== record.validDateEnd

                                if (hasChanged) {
                                    await onUpdate(record.id, data)
                                }
                            }}
                        />
                    )
                }
            </DialogContent>
        </Dialog>
    )
}

