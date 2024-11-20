import PageContainer from "@/layout/PageContainer";
import DataTable from "@/components/DataTable";
import {DateTableColumnProps} from "@/components/DataTable/types";

const columns: Array<DateTableColumnProps<{
    id: string
    name: string
    visitCount: number
}>> = [
    {
        title: "短链id",
        dataIndex: "id"
    },
    {
        title: "短链名称",
        dataIndex: "name"
    },
    {
        title: "访问次数",
        dataIndex: "visitCount"
    },
    {
        title: "访问人数",
        dataIndex: "visitCount"
    },
    {
        title: "Ip数",
        dataIndex: "visitCount"
    },
    {
        title: "操作",
        dataIndex: "visitCount"
    },
]

export default function WelcomePage()
{
    return (
        <PageContainer>
            <DataTable columns={columns} components={{SearchArea: null}}/>
        </PageContainer>
    )
}