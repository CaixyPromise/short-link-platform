// types.ts
import React, {ReactNode} from "react"
import {ButtonVariant} from "@/app/typing";
import {BreathingDotStatus} from "@/components/BreathingDot";


export type DataTableFieldType =
    | 'text'
    | 'number'
    | 'date'
    | 'select'
    | 'checkbox'
    | 'password'
    | 'textarea'
    | 'email'
    | 'url'
    | 'image'
    | 'link'
    | 'option'
    | 'enums'

export type RequestData<T> = {
    data: T[] | undefined
    success?: boolean
    total?: number
} & Record<string, unknown>

export type SortOrder = 'descend' | 'ascend' | null

export type DateTableColumnProps<T> = {
    title: string
    dataIndex?: keyof T
    valueType?: DataTableFieldType
    render?: (text: any, record: T, index: number, dom: ReactNode) => ReactNode
    width?: number
    hideInTable?: boolean
    hideInSearch?: boolean
    copyable?: boolean
    initialValue?: any
    searchType?: DataTableFieldType
    searchRender?: (form: any, fieldProps: any) => ReactNode
    sorter?: boolean
    toolTip?: string | ReactNode
    enumMap?: Record<string, {
        text: string
        status?: BreathingDotStatus
    }>
}
export type DataTableRequestAsyncFunction<T> = (
    params: {
        pageSize?: number
        current?: number
        keyword?: string
    },
    sort: Record<string, SortOrder>,
    filter: Record<string, (string | number)[] | null>
) => Promise<Partial<RequestData<T>>>

export type DataTableContextType<T> = {
    data: T[]
    setData: React.Dispatch<React.SetStateAction<T[]>>
    columns: DateTableColumnProps<T>[]
    visibleColumns: string[]
    setVisibleColumns: React.Dispatch<React.SetStateAction<string[]>>
    isLoading: boolean
    title?:string
    setIsLoading: React.Dispatch<React.SetStateAction<boolean>>
    pagination: {
        current: number
        pageSize: number
        total: number
    }
    setPagination: React.Dispatch<
        React.SetStateAction<{
            current: number
            pageSize: number
            total: number
        }>
    >
    fetchData: (params?: any) => Promise<void>
    sortConfig: Record<string, SortOrder>
    setSortConfig: React.Dispatch<React.SetStateAction<Record<string, SortOrder>>>,
}

export interface DefaultOperationButtonConfig {
    key?: string;
    label: string;
    icon: React.ReactNode;
    onClick?: (() => void) | 'refresh';
    variant?: ButtonVariant;
    hidden?: boolean;
    enable?: boolean;
    type?: 'columnSettings' | 'normal';
}

export interface TableActionBarProps {
    extraButtons?: React.ReactNode[];
    actionButtons?: DefaultOperationButtonConfig[];
}

export type DataTableProps<T> = {
    columns: Array<DateTableColumnProps<T>>
    request?: DataTableRequestAsyncFunction<Array<T>>
    title?:string
    dataSource?: T[]
    initialPagination?: {
        current: number
        pageSize: number
        total: number
    }
    components?: {
        SearchArea?: ReactNode | null
        TableActionBar?: ReactNode | null
        TableContent?: ReactNode | null
        TableFooter?: ReactNode | null
    }
    className?: string
    style?: React.CSSProperties;
}
