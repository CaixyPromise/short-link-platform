"use client"
import PageContainer from "@/layout/PageContainer";
import DataTable from "@/components/DataTable";
import {DateTableColumnProps, RequestData, SortOrder} from "@/components/DataTable/types";
import {ValidDateEnum} from "@/enums/ValidDateEnum";
import {DateUtils} from "@/lib/DateUtils";
import {listLinkVoByPage} from "@/api/linkController";
import BreathingDot from "@/components/BreathingDot";
import {Condition, Conditional} from "@/components/Conditional";
import React, {useEffect, useState} from "react";
import {Button} from "@/components/ui/button";
import CopyableText from "@/components/CopyableText";
import {Plus} from "lucide-react";
import ShortLinkQRCode from "@/app/link/components/ShortLinkQRCode";
import {AddShortLinkForm} from "@/app/link/components/AddShortLinkForm";
import {default_operation_button, TableActionBar} from "@/components/DataTable/components/TableActionBar";
import {useParams, usePathname, useRouter} from 'next/navigation';
import {useDataTable} from "@/components/DataTable/DataTableContext";
import {Tooltip, TooltipContent, TooltipTrigger} from "@/components/ui/tooltip";


const MetricItem: React.FC<{
    label: string;
    value?: number | null;
}> = ({label, value}) => {
    return (
        <div className="flex justify-between">
            <span className="font-medium">{label}:</span>
            <span>{value !== null && value !== undefined ? value : "暂无数据"}</span>
        </div>
    );
};
const columns: Array<DateTableColumnProps<API.LinkVO>> = (onShare: (record: API.LinkVO) => void) => [
    {
        title: "短链id",
        dataIndex: "id"
    },
    {
        title: "短链名称",
        dataIndex: "linkName"
    },
    {
        title: "描述",
        dataIndex: "description",
        valueType: "textarea"
    },
    {
        title: "短链地址",
        dataIndex: "fullShortUrl",
        copyable: true,
        render: (_, record) => {
            return (
                <div className="flex-col space-y-2">
                    <div className="flex items-center gap-2">
                        <span>短链地址: </span>
                        <CopyableText text={record.fullShortUrl}/>
                        <ShortLinkQRCode url={record.fullShortUrl} name={record.linkName}/>
                    </div>
                    <div className="flex items-center gap-2">
                        <span>原始链接: </span>
                        <CopyableText text={record.originUrl}/>
                    </div>
                </div>
            )
        }
    },
    {
        title: "是否启用",
        dataIndex: "enableStatus",
        valueType: "enums",
        enumMap: {
            0: {
                text: "已启用",
                status: "success"
            },
            1: {
                text: "未启用",
                status: "error"
            }
        }
    },
    {
        title: "创建类型",
        dataIndex: "createdType",
        valueType: "enums",
        enumMap: {
            0: {
                text: "接口创建",
            },
            1: {
                text: "控制台创建"
            }
        }
    },
    {
        title: "有效期",
        dataIndex: "validDate",
        align: "center",
        className: "text-center align-middle",
        render: (text, record) => {
            return (
                <div className="flex flex-col items-center">
                    <Conditional>
                        <Condition.Switch value={record.validDateType}>
                            <Condition.Case case={ValidDateEnum.PERMANENT}>
                                <div className="flex items-center gap-2">
                                    <BreathingDot status="success"/>
                                    <span>永久有效</span>
                                </div>
                            </Condition.Case>

                            <Condition.Case case={ValidDateEnum.SPECIFIED}>
                                <div className="flex flex-row items-center gap-4">
                                    {/* 呼吸效果 */}
                                    <BreathingDot
                                        status={
                                            new Date() < new Date(record.validDateStart)
                                                ? "default" // 未开始
                                                : new Date() > new Date(record.validDateEnd)
                                                    ? "error" // 已结束
                                                    : "success" // 有效期内
                                        }
                                    />

                                    {/* 时间信息 */}
                                    <div className="flex flex-col gap-1">
                                        <span>开始时间: {DateUtils.formatDate(record.validDateStart, "YYYY-MM-DD HH:mm:ss")}</span>
                                        <span>结束时间: {DateUtils.formatDate(record.validDateEnd, "YYYY-MM-DD HH:mm:ss")}</span>
                                    </div>
                                </div>
                            </Condition.Case>


                        </Condition.Switch>
                    </Conditional>
                </div>
            )
        }
    },

    {
        title: "数据指标",
        toolTip: (
            <>
                <p>历史 PV: 短链被访问的次数</p>
                <p>历史 UV: 短链被独立用户访问的次数</p>
                <p>历史 UIP: 短链被独立 IP 访问的次数</p>
            </>
        ),
        render: (_, record) => {
            const {totalPv, totalUv, totalUip, clickNum} = record;
            return (<div className="space-y-2">
                <MetricItem label="历史 PV" value={totalPv}/>
                <MetricItem label="历史 UV" value={totalUv}/>
                <MetricItem label="历史 UIP" value={totalUip}/>
                <MetricItem label="点击量" value={clickNum}/>
            </div>)
        }
    },
    {
        title: "操作",
        render: () => {
            return (
                <div className="flex gap-1">
                    <Button variant="link">编辑</Button>
                    <Button variant="link" className="text-red-600">删除</Button>
                </div>
            )
        }
    },
]

export default function LinkTablePage() {
    const [qrCodeVisible, setQrCodeVisible] = useState<boolean>(false);
    const [qrCodeUrl, setQrCodeUrl] = useState<string>('');
    const [qrCodeName, setQrCodeName] = useState<string>('');
    const [shortModalVisible, setShortModalVisible] = useState<boolean>(false);

    const params = useParams();
    const {gid: groupId} = params;
    const router = useRouter();

    useEffect(() => {
        if (!groupId) {
            router.replace('/');
        }
    }, [groupId]);

    const fetchLink = async (params: any,
                             sort: Record<string, SortOrder>,
                             filter: Record<string, (string | number)[] | null>
    ): Promise<Partial<RequestData<API.LinkVO[]>>> => {
        const {data, code} = await listLinkVoByPage(params);
        return {
            success: code === 0,
            data: data?.records || [],
            total: Number(data?.total) || 0,
        }
    }

    const createShareQrCode = (record: API.LinkVO) => {
        setQrCodeUrl(record.fullShortUrl);
        setQrCodeName(record.linkName);
        setQrCodeVisible(true);
    }


    const handleShortLinkModalVisible = (openState: boolean) => {
        setShortModalVisible(openState);
    }


    return (
        <PageContainer>
            <DataTable<API.LinkVO>
                title="短链列表"
                columns={columns(createShareQrCode)}
                request={fetchLink}
                components={{
                    SearchArea: null,
                    TableActionBar: <TableActionBar
                        actionButtons={[
                            {
                                key: 'add',
                                label: "添加",
                                icon: <Plus className="mr-2 h-4 w-4"/>,
                                onClick: () => {
                                    console.log("add");
                                    setShortModalVisible(true)
                                },
                                variant: "default"
                            },
                            ...default_operation_button.filter(item => item.key !== "add"),
                        ]}/>
                }}
            />
            <AddShortLinkForm open={shortModalVisible} setOpen={setShortModalVisible} groupId={groupId}/>
            {/*<ShortLinkQRCode name={qrCodeName} url={qrCodeUrl}/>*/}
        </PageContainer>
    )
}