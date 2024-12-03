"use client"
import PageContainer from "@/layout/PageContainer";
import DataTable from "@/components/DataTable";
import {DateTableColumnProps, RequestData, SortOrder} from "@/components/DataTable/types";
import {ValidDateEnum} from "@/enums/ValidDateEnum";
import {DateUtils} from "@/lib/DateUtils";
import {listLinkVoByPage, updateLinkStatus} from "@/api/linkController";
import BreathingDot from "@/components/BreathingDot";
import {Condition, Conditional} from "@/components/Conditional";
import React, {useEffect, useState} from "react";
import {Button} from "@/components/ui/button";
import CopyableText from "@/components/CopyableText";
import {ArrowBigRight, Info, Plus} from "lucide-react";
import ShortLinkQRCode from "@/app/link/components/ShortLinkQRCode";
import {AddShortLinkForm} from "@/app/link/components/AddShortLinkForm";
import {default_operation_button, TableActionBar} from "@/components/DataTable/components/TableActionBar";
import {useParams, useRouter} from 'next/navigation';
import {StatusIndicator} from "@/app/link/components/StatusIndicator";
import {ValidityPeriodCell} from "@/app/link/components/ValidityCell";
import {useAppDispatch, useAppSelector} from "@/stores/hooks";
import {onChangeGroupClick, updateCurrentGroupByGid} from "@/stores/Group";
import UpdateGroupModal from "@/app/link/components/UpdateGroupModal";
import {Empty} from "@/components/Empty/empty";
import {EmptyIcon} from "@/components/Empty/empty-icon";
import {EmptyDescription} from "@/components/Empty/empty-description";
import EmptyDescriptionText from "@/components/Empty/empty-description-text";


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
const columns: Array<DateTableColumnProps<API.LinkVO>> = (
    onShare: (record: API.LinkVO) => void,
    groupId: string,
    onClickValidTimeCell: (record: API.LinkVO) => void,
    onClickUpdateGroupModal: (record: API.LinkVO) => void
) => ([
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
            const linkText = process.env.NODE_ENV === "development"
                ? record.fullShortUrl.replace(/(https?:\/\/[^/]+)(\/.*)?/, "$1/api/s$2")
                : record.fullShortUrl;

            return (
                <div className="flex-col space-y-2">
                    <div className="flex items-center gap-2">
                        <span>短链地址: </span>
                        <CopyableText type="link" text={linkText}/>
                        <ShortLinkQRCode url={linkText} name={record.linkName}/>
                    </div>
                    <div className="flex items-center gap-2">
                        <span>原始链接: </span>
                        <CopyableText type="link" text={record.originUrl}/>
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
        },
        render: (_, record) => {
            return (
                <StatusIndicator
                    enabled={record.enableStatus === 0}
                    onToggle={async (enabled) => {
                        try {
                            const res = await updateLinkStatus({
                                linkId: record.id,
                                groupId: groupId,
                                status: enabled ? 0 : 1
                            })
                            if (res.code === 0) {
                                return {
                                    success: true,
                                    newValue: enabled,
                                    message: `${enabled ? '启用' : '禁用'}成功`
                                }
                            }
                            return {
                                success: false,
                                newValue: !enabled,
                                message: res.message || '操作失败'
                            }
                        } catch (error) {
                            return {
                                success: false,
                                newValue: !enabled,
                                message: '网络错误，请稍后重试'
                            }
                        }
                    }}
                />
            )
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
                <div className="flex flex-col items-center cursor-pointer transition-all duration-200 hover:bg-accent/10  rounded-md p-2 hover:ring-1"
                     onClick={(e) => {
                    e.stopPropagation()
                    onClickValidTimeCell(record)
                }}>
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
        render: (_, record) => {
            return (
                <div className="flex gap-1">
                    <Button variant="link" onClick={()=>onClickUpdateGroupModal(record)}>更换分组</Button>
                    <Button variant="link" className="text-red-600">删除</Button>
                </div>
            )
        }
    },
]);

export default function LinkTablePage() {
    const [qrCodeVisible, setQrCodeVisible] = useState<boolean>(false);
    const [qrCodeUrl, setQrCodeUrl] = useState<string>('');
    const [qrCodeName, setQrCodeName] = useState<string>('');
    const [shortModalVisible, setShortModalVisible] = useState<boolean>(false);
    const [validFormModalVisible, setValidFormModalVisible] = useState<boolean>(false);
    const [currentRow, setCurrentRow] = useState<API.LinkVO | null>(null);
    const [ updateGroupModalVisible ,setUpdateGroupModalVisible] = useState<boolean>(false)
    const params = useParams();
    const {gid: groupId} = params;
    const router = useRouter();
    const dispatch = useAppDispatch();
    const currentGroup = useAppSelector(state => state.Group);

    useEffect(() => {
        if (!groupId) {
            router.replace('/');
            return;
        }
        // 只在 groupId 初始化时检查和更新 currentGroup
        if (!currentGroup.currentGroupId || currentGroup.currentGroupId !== groupId) {
            dispatch(updateCurrentGroupByGid(groupId));
        }
    }, [groupId, dispatch, router]);


    const fetchLink = async (params: any,
                             sort: Record<string, SortOrder>,
                             filter: Record<string, (string | number)[] | null>
    ): Promise<Partial<RequestData<API.LinkVO[]>>> => {
        const {data, code} = await listLinkVoByPage({
            gid: groupId,
            ...params
        });
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

    // 点击选项时更新当前选项并显示弹窗
    const onClickValidTimeCell = (record: API.LinkVO) => {
        setCurrentRow(record); // 更新当前行
        setValidFormModalVisible(true); // 打开弹窗
    };

    // 关闭弹窗时重置状态
    const closeValidDateModal = () => {
        setValidFormModalVisible(false);
        setCurrentRow(null); // 重置当前行
    };

    const closeUpdateGroupModal = () => {
        setUpdateGroupModalVisible(false);
        setCurrentRow(null); // 重置当前行
    }

    const onClickUpdateGroupModal= (record: API.LinkVO) => {
        setUpdateGroupModalVisible(true);
        setCurrentRow(record); // 设置当前行
    }



    return (
        <PageContainer>
            <DataTable<API.LinkVO>
                title={`${currentGroup?.currentGroupName}-短链列表`}
                columns={columns(createShareQrCode, groupId, onClickValidTimeCell, onClickUpdateGroupModal)}
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
                        ]}/>,
                    TableEmpty: (
                        <Empty>
                            <EmptyIcon>
                                <Info className="text-blue-500 w-20 h-20 text-muted-foreground" />
                            </EmptyIcon>
                            <EmptyDescription>
                                当前分组: {currentGroup.currentGroupName} 暂无链接数据 :)
                            </EmptyDescription>
                            <EmptyDescription>
                                <Button
                                    onClick={() => {
                                        setShortModalVisible(true)
                                    }}
                                    variant="ghost"
                                    className="text-red-600"
                                >
                                    <ArrowBigRight />
                                    去创建
                                </Button>
                            </EmptyDescription>
                        </Empty>
                    )
                }}
            />
            <AddShortLinkForm open={shortModalVisible} setOpen={setShortModalVisible} groupId={groupId}/>
            <ValidityPeriodCell
                record={currentRow}
                open={validFormModalVisible}
                onClose={closeValidDateModal} // 弹窗关闭回调
            />
            <UpdateGroupModal
                linkId={currentRow?.id}
                open={updateGroupModalVisible}
                onOpenChange={closeUpdateGroupModal}
                originGroupId={groupId}
            />
            {/*<ShortLinkQRCode name={qrCodeName} url={qrCodeUrl}/>*/}
        </PageContainer>
    )
}