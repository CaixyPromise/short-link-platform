
// 定义列配置
import {DateTableColumnProps} from "@/components/DataTable/types";
import {Button} from "@/components/ui/button";
import React from "react";

export type User = {
    id: string
    userAccount: string
    userName: string
    userPhone: string
    userEmail: string
    userGender: string
    userAvatar: string
    userProfile: string
    userRole: string
    createTime: string
    updateTime: string
}

export const columns: DateTableColumnProps<User>[] = [
    {
        title: "ID",
        dataIndex: "id",
        valueType: "text",
        copyable: true,
        sorter: true,
    },
    {
        title: "账号",
        dataIndex: "userAccount",
        valueType: "text",
        hideInSearch: true,
    },
    {
        title: "用户名",
        dataIndex: "userName",
        valueType: "text",
    },
    {
        title: "用户手机号",
        dataIndex: "userPhone",
        valueType: "text",
    },
    {
        title: "用户邮箱",
        dataIndex: "userEmail",
        valueType: "email",
    },
    {
        title: "用户性别",
        dataIndex: "userGender",
        valueType: "select",
        initialValue: [
            { value: "male", label: "男" },
            { value: "female", label: "女" },
            { value: "unknown", label: "未知" },
        ],
        searchType: "select",
    },
    {
        title: "头像",
        dataIndex: "userAvatar",
        valueType: "image",
        hideInSearch: true,
    },
    {
        title: "简介",
        dataIndex: "userProfile",
        valueType: "textarea",
        hideInSearch: true,
    },
    {
        title: "权限",
        dataIndex: "userRole",
        valueType: "select",
        initialValue: [
            { value: "admin", label: "管理员" },
            { value: "user", label: "普通用户" },
            { value: "guest", label: "访客" },
        ],
        searchType: "select",
    },
    {
        title: "创建时间",
        dataIndex: "createTime",
        valueType: "date",
        sorter: true,
    },
    {
        title: "更新时间",
        dataIndex: "updateTime",
        valueType: "date",
        sorter: true,
        hideInSearch: true,
    },
    {
        title: "操作",
        dataIndex: "handler",
        valueType: "option",
        render: (text, record) => (
            <div className="flex">
                <Button variant="link">修改</Button>
                <Button variant="link" className="text-destructive">
                    删除
                </Button>
            </div>
        ),
        hideInSearch: true
    }
]
