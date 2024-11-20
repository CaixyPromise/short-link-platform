"use client"
import PageContainer from "@/layout/PageContainer";
import DataTable from "@/components/DataTable";
import {DateTableColumnProps, RequestData, SortOrder} from "@/components/DataTable/types";
import useAsyncHandler from "@/hooks/useAsyncHandler";
import {useState} from "react";
import {listMyGroupVoByPage} from "@/api/groupController";
import {useToast} from "@/hooks/use-toast";
import {OnlineUser} from "@/app/admin/online/column";
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle} from "@/components/ui/dialog";
import {DialogBody} from "next/dist/client/components/react-dev-overlay/internal/components/Dialog";
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {
    default_operation_button,
    getTableButtonConfig,
    TableActionBar
} from "@/components/DataTable/components/TableActionBar";
import AddGroupFormModal from "@/app/group/components/AddGroupFormModal";
import {Plus} from "lucide-react";

const columns: Array<DateTableColumnProps<{
    id: string
    name: string
    gid: string
    sortOrder: string,
    createTime: string
    updateTime: string
}>> = [
    {
        title: "分组id",
        dataIndex: "id"
    },
    {
        title: "分组标识符",
        dataIndex: "gid"
    },
    {
        title: "分组名称",
        dataIndex: "name"
    },
    {
        title: "分组排序权重",
        dataIndex: "sortOrder",
        render: (text) => {
            return <p>{text}</p>
        }
    },
    {
        title: "创建时间",
        dataIndex: "createTime",
        valueType: "date"
    },
    {
        title: "更新时间",
        dataIndex: "updateTime",
        valueType: "date"
    },
    {
        title: "操作",
        render: (text, record) => {
            console.log(text)
            return <div>
                <Button variant="link" >
                    编辑
                </Button>
                <Button variant="link" className="text-red-500">
                    删除
                </Button>

            </div>
        }
    },
]

export default function GroupPage()
{
    const [addGroupModalVisible, setAddGroupModalVisible] = useState<boolean>(false)
    const fetchUserData = async (params: any,
                                 sort: Record<string, SortOrder>,
                                 filter: Record<string, (string | number)[] | null>
    ): Promise<Partial<RequestData<API.GroupVO[]>>> => {
        const {data, code} = await listMyGroupVoByPage(params);
        return {
            success: code === 0,
            data: data?.records || [],
            total: Number(data?.total) || 0,
        }
    }

    const onAddClick = () => {
        setAddGroupModalVisible(true)
    }

    return (
        <PageContainer >
            <DataTable<API.GroupVO>
                request={fetchUserData}
                columns={columns}
                components={{
                    SearchArea: null,
                    TableActionBar: <TableActionBar
                        actionButtons={[
                            {
                                key: 'add',
                                label: "添加",
                                icon: <Plus className="mr-2 h-4 w-4" />,
                                onClick: onAddClick,
                                variant: "default"
                            },
                            ...default_operation_button.filter(item=>item.key !== "add"),
                        ]}
                    />
                }}
            />

            <AddGroupFormModal
                addGroupModalVisible={addGroupModalVisible}
                setAddGroupModalVisible={setAddGroupModalVisible}
            />
        </PageContainer>
    )
}