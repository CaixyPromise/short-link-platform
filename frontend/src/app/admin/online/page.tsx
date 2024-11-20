"use client"

import React from 'react';
import {RequestData, SortOrder} from "@/components/DataTable/types";

import {columns, OnlineUser} from "@/app/admin/online/column";
import DataTable from "@/components/DataTable";
import {forceLogout, getOnlineUsers} from "@/api/adminController";
import useAsyncHandler from "@/hooks/useAsyncHandler";


const OnlinePage: React.FC = () =>
{
    const [forceLogoutHandler] = useAsyncHandler();
    const fetchUserData = async (params: any,
                                 sort: Record<string, SortOrder>,
                                 filter: Record<string, (string | number)[] | null>
    ): Promise<Partial<RequestData<OnlineUser>>> => {
        const sortField = Object.keys(sort)?.[0];
        const sortOrder = sort?.[sortField] ?? undefined;

        const response = await getOnlineUsers({
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

    const handleForceLogout = async (user: OnlineUser) =>
    {
        return await forceLogoutHandler(async () =>
        {
            const {code} = forceLogout({
                userId: user.id
            })
            return code === 0;
        }, [])
    }


    return (
        <div className="container mx-auto px-4 py-8 mb-8">
            <DataTable<OnlineUser>
                title="在线用户管理"
                columns={columns(handleForceLogout)}
                request={fetchUserData}
                // 可以传递自定义的组件或设置为 null
                components={{
                    SearchArea: null, // 不渲染搜索区域
                }}
            />
        </div>
    )
}

export default OnlinePage;
