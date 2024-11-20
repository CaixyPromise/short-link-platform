// SearchAreaBar.tsx

import React, { useState } from "react"
import { useForm } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { ChevronDown, ChevronUp } from "lucide-react"
import {useDataTable} from "@/components/DataTable/DataTableContext";
import {DateTableColumnProps} from "@/components/DataTable/types";


export const SearchAreaBar: React.FC = () => {
    const { columns, fetchData, setPagination } = useDataTable()
    const { register, handleSubmit, reset, control } = useForm()
    const [isSearchExpanded, setIsSearchExpanded] = useState<boolean>(true)

    const searchColumns = columns.filter(col => !col.hideInSearch)

    const onSubmit = (data: never) => {
        setPagination(prev => ({ ...prev, current: 1 }))
        fetchData(data)
    }

    const renderSearchField = (column: DateTableColumnProps<never>) => {
        const fieldType = column.searchType || column.valueType || 'text'

        if (column.searchRender) {
            return column.searchRender({ register, control }, column)
        }

        switch (fieldType) {
            case 'text':
                return (
                    <Input
                        key={column.dataIndex as string}
                        placeholder={column.title}
                        {...register(column.dataIndex as string)}
                    />
                )
            case 'select':
                return (
                    <Select
                        key={column.dataIndex as string}
                        onValueChange={(value) => {
                            // 设置表单值
                        }}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder={column.title} />
                        </SelectTrigger>
                        <SelectContent>
                            {column.initialValue && Array.isArray(column.initialValue)
                                ? column.initialValue.map((option: any) => (
                                    <SelectItem key={option.value} value={option.value}>
                                        {option.label}
                                    </SelectItem>
                                ))
                                : null}
                        </SelectContent>
                    </Select>
                )
            // 可以根据需要扩展其他类型
            default:
                return null
        }
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>搜索</CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                    <div
                        className={`grid gap-4 ${
                            isSearchExpanded ? 'grid-cols-4' : 'grid-cols-3'
                        }`}
                    >
                        {searchColumns
                            .slice(0, isSearchExpanded ? searchColumns.length : 3)
                            .map(renderSearchField)}
                    </div>
                    <div className="flex justify-end space-x-2">
                        <Button type="reset" variant="outline" onClick={() => reset()}>
                            重置
                        </Button>
                        <Button type="submit" variant="secondary">
                            查询
                        </Button>
                        <Button
                            type="button"
                            variant="outline"
                            onClick={() => setIsSearchExpanded(!isSearchExpanded)}
                        >
                            {isSearchExpanded ? (
                                <ChevronUp className="mr-2 h-4 w-4" />
                            ) : (
                                <ChevronDown className="mr-2 h-4 w-4" />
                            )}
                            {isSearchExpanded ? '收起' : '展开'}
                        </Button>
                    </div>
                </form>
            </CardContent>
        </Card>
    )
}
