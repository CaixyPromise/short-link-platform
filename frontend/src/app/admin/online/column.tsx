import {DateTableColumnProps} from "@/components/DataTable/types";
import {
    AlertDialog,
    AlertDialogContent,
    AlertDialogHeader,
    AlertDialogFooter,
    AlertDialogCancel,
    AlertDialogAction, AlertDialogTrigger
} from "@/components/ui/alert-dialog";
import {Button} from "@/components/ui/button";
import React from "react";
export type OnlineUser = {
    sessionId: string;
    id: string;
    userName: string;
    userRole: string;
    loginTime: number;
    loginIp: string;
    os: number;
    browser: string;
    loginLocation: string;
    expireTime: string;
}


export const columns = (forceLogout: (user: OnlineUser) => Promise<void>): Array<DateTableColumnProps<OnlineUser>> => {
    return [

        {
            title: "会话ID",
            dataIndex: "sessionId",
            valueType: "text",
            copyable: true
        },
        {
            title: "用户ID",
            dataIndex: "id",
            valueType: "text",
            copyable: true
        },
        {
            title: "用户名",
            dataIndex: "userName",
            valueType: "text",
        },
        {
            title: "权限",
            dataIndex: "userRole",
            valueType: "select",
            initialValue: [
                {value: "admin", label: "管理员"},
                {value: "user", label: "普通用户"},
                {value: "guest", label: "访客"},
            ],
            searchType: "select",
        },
        {
            title: "登录操作系统",
            dataIndex: "os",
            valueType: "text",
        },
        {
            title: "登录浏览器",
            dataIndex: "browser",
            valueType: "text",
        },
        {
            title: "登录IP地点",
            dataIndex: "loginLocation",
            valueType: "text",
        },
        {
            title: "登录IP",
            dataIndex: "loginIp",
            valueType: "text",
        },
        {
            title: "操作",
            dataIndex: "operation",
            valueType: "option",
            render: (text, record) => (
                <div className="flex">
                    <AlertDialog>
                        <AlertDialogTrigger asChild>
                            <Button
                                variant="link"
                                className="text-destructive"
                            >
                                强制下线
                            </Button>
                        </AlertDialogTrigger>
                        <AlertDialogContent>
                            <AlertDialogHeader>
                                <h2>Confirm</h2>
                                <p>Are you certain that you will force the user  {record.userName} to go offline?</p>
                            </AlertDialogHeader>
                            <AlertDialogFooter>
                                <AlertDialogCancel>Cancel</AlertDialogCancel>
                                <AlertDialogAction onClick={() => forceLogout(record)}>Confirm</AlertDialogAction>
                            </AlertDialogFooter>
                        </AlertDialogContent>
                    </AlertDialog>
                </div>
            ),
            hideInSearch: true
        },
    ]

}
