// TableContent.tsx

import React, {ReactNode} from "react"
import {CardContent} from "@/components/ui/card"
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {Button} from "@/components/ui/button"
import {Loader2, Copy, CircleUser, AlertCircle} from "lucide-react"
import {useDataTable} from "@/components/DataTable/DataTableContext";

import {copyToClipboard} from "@/lib/copyToClipboard"
import {DateTableColumnProps} from "@/components/DataTable/types";
import {Condition, Conditional} from "@/components/Conditional";
import {DateUtils} from "@/lib/DateUtils";
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import BreathingDot from "@/components/BreathingDot";
import {Tooltip, TooltipContent, TooltipTrigger} from "@/components/ui/tooltip";
import CopyableText from "@/components/CopyableText";

export const TableContent: React.FC = () =>
{
    const {data, columns, visibleColumns, isLoading, sortConfig, setSortConfig} =
        useDataTable()

    const handleSort = (dataIndex: string) =>
    {
        const sortOrder = sortConfig[dataIndex] === 'ascend' ? 'descend' : 'ascend'
        setSortConfig({[dataIndex]: sortOrder})
    }

    const renderCellContent = (
        column: DateTableColumnProps<any>,
        record: any,
        index: number
    ) => {
        if (!column.dataIndex && !column.render) {
            return null
        }
        const text = record[column.dataIndex]

        let content: ReactNode = text
        if (column.valueType === 'number') {
            content = <span>{Number(text).toLocaleString()}</span>
        }

        if (column.valueType === 'image' && typeof text === 'string') {
            content = (
                <Avatar>
                    <AvatarImage src={text} alt={record.username}/>
                    <AvatarFallback><CircleUser className="w-full h-full"/></AvatarFallback>
                </Avatar>
            )
        }
        if (column.valueType === "password") {
            content = (
                <div className="flex items-center">
                    <span className="text-gray-500">******</span>
                    <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => copyToClipboard(text)}
                    >
                        <Copy className="h-4 w-4"/>
                    </Button>
                </div>

            )
        }

        if (column.valueType === 'email' && typeof text === 'string') {
            content = (
                <a href={`mailto:${text}`} className="text-blue-500 hover:underline">
                    {text}
                </a>
            )
        }
        if (column.valueType === 'link' && typeof text === 'string') {
            content = (
                <a href={text} className="text-blue-500 hover:underline">
                    {text}
                </a>
            )
        }
        if (column.valueType === 'date' && typeof text === 'string') {
            content = (
                <span>{DateUtils.formatDate(text)}</span>
            )
        }
        if (column.valueType === "enums" && column.enumMap) {
            const enumObject =  column.enumMap[text];
            content = (
                <div className="flex items-center gap-2">
                    {enumObject.status && <BreathingDot status={enumObject.status}/>}
                    <span>{enumObject.text}</span>
                </div>
            )
        }

        if (column.copyable) {
            content = (
                <CopyableText text={text}/>
            )
        }

        if (!content) {
            content = <span>-</span>
        }

        if (column.render) {
            return column.render(text, record, index, content)
        }
        return content
    }

    return (
        <CardContent>
            <Conditional value={isLoading}>
                <Condition.When test={isLoading}>
                    <div className="flex justify-center items-center h-64">
                        <Loader2 className="h-8 w-8 animate-spin mr-2.5"/>
                        正在加载数据中......
                    </div>
                    <Condition.Else>
                        <Table>
                            <TableHeader>
                            <TableRow>
                            {columns.filter(column => visibleColumns.includes(column.dataIndex as string))
                                .map(column => (
                                <TableHead key={column.dataIndex as string}>
                                    <div
                                        className={`flex items-center ${
                                            column.sorter ? 'cursor-pointer select-none' : ''
                                        }`}
                                        onClick={() => column.sorter && handleSort(column.dataIndex as string)}
                                    >
                                        <Conditional value={column.toolTip}>
                                            <Tooltip>
                                                <TooltipTrigger asChild>
                                                    <button
                                                        className="mr-1 inline-flex items-center justify-center rounded-full w-4 h-4 bg-muted text-muted-foreground hover:bg-muted/80 hover:text-muted-foreground/80 active:bg-muted/60 active:text-muted-foreground/60 transition-colors"
                                                        aria-label="More information"
                                                    >
                                                        <AlertCircle className="w-3 h-3"/>
                                                    </button>
                                                </TooltipTrigger>
                                                <TooltipContent>
                                                    {column.toolTip}
                                                </TooltipContent>
                                            </Tooltip>
                                        </Conditional>
                                        {column.title}
                                        <Conditional value={column.sorter}>
                                            <span className="ml-1 text-xs">
                                                <Conditional value={sortConfig[column.dataIndex as string]}>
                                                    <Condition.When
                                                        test={sortConfig[column.dataIndex as string] === 'ascend'}>
                                                        ↑
                                                    </Condition.When>
                                                    <Condition.When
                                                        test={sortConfig[column.dataIndex as string] === 'descend'}>
                                                        ↓
                                                    </Condition.When>
                                                    <Condition.Else>
                                                        ↕
                                                    </Condition.Else>
                                                </Conditional>
                                            </span>
                                        </Conditional>
                                    </div>
                                </TableHead>
                                ))}
                                {/*<TableHead>操作</TableHead>*/}
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {data.map((item: any, index: number) => (
                                <TableRow key={item.id || index}>
                                    {columns
                                        .filter(column =>
                                            visibleColumns.includes(column.dataIndex as string)
                                        )
                                        .map(column => (
                                            <TableCell key={column.dataIndex as string}>
                                                {renderCellContent(column, item, index)}
                                            </TableCell>
                                        ))}
                                </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </Condition.Else>
                </Condition.When>
            </Conditional>
        </CardContent>
    )
}
