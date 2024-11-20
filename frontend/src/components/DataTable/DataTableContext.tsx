// DataTableContext.tsx

import React, {
    createContext,
    useState,
    useEffect,
    useCallback,
    useContext,
} from "react"
import {DataTableContextType, DataTableProps, DateTableColumnProps, SortOrder} from "./types"



const DataTableContext = createContext<DataTableContextType<never> | undefined>(
    undefined
)

type DataTableProviderProps<T> = {
    columns: DateTableColumnProps<T>[]
    request?: DataTableProps<T>["request"]
    dataSource?: T[]
    title?:string
    initialPagination?: {
        current: number
        pageSize: number
        total: number
    }
    children: React.ReactNode,
}

export const DataTableProvider = <T extends Record<string, any>>({
    columns,
    request,
    dataSource = [],
    initialPagination = { current: 1, pageSize: 10, total: 0 },
    children,
    title
}: DataTableProviderProps<T>) => {
    const [data, setData] = useState<Array<T>>(dataSource)
    const [visibleColumns, setVisibleColumns] = useState<string[]>(
        columns.filter(c => !c.hideInTable).map(c => c.dataIndex as string)
    )
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [pagination, setPagination] = useState(initialPagination)
    const [sortConfig, setSortConfig] = useState<Record<string, SortOrder>>({})

    const defaultRequest = async (
        params: any,
        sort: Record<string, SortOrder>,
        filter: Record<string, (string | number)[] | null>
    ) => { // 默认请求实现
        return { data: [], total: 0, success: true }
    }

    const fetchData = useCallback(
        async (params = {}) => {
            setIsLoading(true)
            try {
                const result = await (request || defaultRequest)(
                    {
                        ...params,
                        pageSize: pagination.pageSize,
                        current: pagination.current,
                    },
                    sortConfig,
                    {} // filter 可根据需要实现
                )
                if (result.success && result.data) {
                    setData(result.data)
                    setPagination(prev => ({ ...prev, total: result.total || 0 }))
                }
            } catch (error) {
                console.error("Failed to fetch data:", error)
            } finally {
                setIsLoading(false)
            }
        },
        [request, pagination.current, pagination.pageSize, sortConfig]
    )

    useEffect(() => {
        fetchData()
    }, [pagination.current, pagination.pageSize, sortConfig])

    return (
        <DataTableContext.Provider
            value={{
                title,
                data,
                setData,
                columns,
                visibleColumns,
                setVisibleColumns,
                isLoading,
                setIsLoading,
                pagination,
                setPagination,
                fetchData,
                sortConfig,
                setSortConfig,
            }}
        >
            {children}
        </DataTableContext.Provider>
    )
}

export const useDataTable = <T extends Record<string, any>>(): DataTableContextType<T> => {
    const context = useContext(DataTableContext)
    if (!context) {
        throw new Error("useDataTable must be used within a DataTableProvider")
    }
    return context
}
