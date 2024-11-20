"use client"
import React from 'react';
import {DateTableColumnProps, RequestData, SortOrder} from "@/components/DataTable/types";
import {Button} from "@/components/ui/button";
import DataTable from "@/components/DataTable";
import {listUserByPage} from "@/api/userController";
import {columns, User} from "@/app/admin/user/column";

const mockRequest = async (params: any,
                           sort: Record<string, SortOrder>,
                           filter: Record<string, (string | number)[] | null>
): Promise<Partial<RequestData<User>>> =>
{
    await new Promise(resolve => setTimeout(resolve, 1000)) // Simulate API delay
    const mockUsers: User[] = Array(150).fill(null).map((_, index) => ({
        id: `user_${index + 1}`,
        userAccount: `account_${index + 1}`,
        userName: `User ${index + 1}`,
        userPhone: `123-456-${index.toString().padStart(4, '0')}`,
        userEmail: `user${index + 1}@example.com`,
        userGender: ['male', 'female', 'unknown'][Math.floor(Math.random() * 3)],
        userAvatar: "/placeholder.svg?height=40&width=40",
        userProfile: `Bio for User ${index + 1}`,
        userRole: ['admin', 'user', 'guest'][Math.floor(Math.random() * 3)],
        createTime: new Date(Date.now() - Math.random() * 10000000000).toISOString(),
        updateTime: new Date().toISOString(),
    }))

    const {pageSize = 10, current = 1} = params
    const startIndex = (current - 1) * pageSize
    const endIndex = startIndex + pageSize

    return {
        data: mockUsers.slice(startIndex, endIndex),
        total: mockUsers.length,
        success: true,
    }
}
const Table: React.FC = () =>
{
    const fetchUserData = async (params: any,
                                 sort: Record<string, SortOrder>,
                                 filter: Record<string, (string | number)[] | null>
    ): Promise<Partial<RequestData<User>>> => {
        const sortField = Object.keys(sort)?.[0];
        const sortOrder = sort?.[sortField] ?? undefined;

        const response = await listUserByPage({
            ...params,
            sortField,
            sortOrder,
            ...filter,
        })
        const { data, code } = response;
        return {
            success: code === 0,
            data: data?.records || [],
            total: Number(data?.total) || 0,
        };
    }


    return (
        <div className="container mx-auto px-4 py-8 mb-8">
            <DataTable<User>
                title="用户管理"
                columns={columns}
                request={fetchUserData}
                // 可以传递自定义的组件或设置为 null
                components={{
                    // SearchAreaBar: null, // 不渲染搜索区域
                    // TableActionBar: <DataTableActionBar />, // 使用自定义的表格操作组件
                }}
            />
        </div>
    )
}

export default Table;
