// TableFooter.tsx

import React, { useState, useMemo, useCallback, useEffect } from "react"
import { CardFooter } from "@/components/ui/card"
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination"
import { Input } from "@/components/ui/input"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import {useDataTable} from "@/components/DataTable/DataTableContext";
import useDebounce from "@/hooks/useDebounce"
import {Condition, Conditional} from "@/components/Conditional";

export const TableFooter: React.FC = () => {
    const { pagination, setPagination, fetchData } = useDataTable()
    const [jumpToPage, setJumpToPage] = useState('')
    const totalPages = useMemo(() => {
        return Math.ceil(pagination.total / pagination.pageSize)
    }, [pagination.total, pagination.pageSize])

    const handlePageChange = (page: number) => {
        setPagination(prev => ({ ...prev, current: page }))
        fetchData()
    }

    const handlePageSizeChange = (newPageSize: number) => {
        setPagination(prev => ({ ...prev, pageSize: newPageSize, current: 1 }))
        fetchData()
    }

    const handleJumpToPage = useCallback(() => {
        const pageNumber = parseInt(jumpToPage, 10)
        if (!isNaN(pageNumber)) {
            if (pageNumber >= 1 && pageNumber <= totalPages) {
                handlePageChange(pageNumber)
            } else {
                setJumpToPage(`${pagination.current}`)
            }
        }
    }, [jumpToPage, totalPages])

    const [debouncedJumpToPage] = useDebounce(handleJumpToPage, 1000)

    useEffect(() => {
        debouncedJumpToPage()
    }, [jumpToPage, debouncedJumpToPage])

    const renderPaginationItems = () => {
        const currentPage = pagination.current
        const items = []

        const addPageItem = (pageNum: number) => {
            items.push(
                <PaginationItem key={pageNum}>
                    <PaginationLink
                        onClick={() => handlePageChange(pageNum)}
                        isActive={currentPage === pageNum}
                    >
                        {pageNum}
                    </PaginationLink>
                </PaginationItem>
            )
        }

        addPageItem(1)

        if (currentPage > 3) {
            items.push(<PaginationEllipsis key="ellipsis-start" />)
        }

        for (
            let i = Math.max(2, currentPage - 1);
            i <= Math.min(totalPages - 1, currentPage + 1);
            i++
        ) {
            addPageItem(i)
        }

        if (currentPage < totalPages - 2) {
            items.push(<PaginationEllipsis key="ellipsis-end" />)
        }

        if (totalPages > 1) {
            addPageItem(totalPages)
        }

        return items
    }

    return (
        <CardFooter>
            <div className="text-sm text-muted-foreground sm:inline-block whitespace-nowrap">
                总共 {pagination.total} 条数据，当前第 {pagination.current} 页
            </div>
            <Pagination>
                    <Conditional value={pagination}>
                        <Condition.When test={pagination.total > 1}>
                            <PaginationContent>

                            <PaginationItem>
                                <PaginationPrevious
                                    onClick={() => handlePageChange(Math.max(1, pagination.current - 1))}
                                />
                            </PaginationItem>
                            {renderPaginationItems()}
                            <PaginationItem>
                                <PaginationNext
                                    onClick={() => handlePageChange(Math.min(Math.ceil(pagination.total / pagination.pageSize), pagination.current + 1))}/>
                            </PaginationItem>
                            <PaginationItem>
                                <div className="flex items-center space-x-2">
                                    <span className="mx-2">跳转到</span>
                                    <Input
                                        type="number"
                                        min={1}
                                        max={totalPages}
                                        placeholder="页数"
                                        value={jumpToPage}
                                        onChange={(e) => setJumpToPage(e.target.value)}
                                        className="w-20 h-8 text-center appearance-none"
                                    />
                                </div>
                            </PaginationItem>
                            </PaginationContent>

                        </Condition.When>
                    </Conditional>
            </Pagination>
            <div className="flex items-center space-x-2">
                <Select
                    value={pagination.pageSize.toString()}
                    onValueChange={(value) => handlePageSizeChange(Number(value))}
                >
                    <SelectTrigger className="w-[70px]">
                        <SelectValue placeholder={pagination.pageSize}/>
                    </SelectTrigger>
                    <SelectContent>
                        {[10, 20, 30, 40, 50].map((size) => (
                            <SelectItem key={size} value={size.toString()}>
                                {size}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
                <span className="sm:inline-block whitespace-nowrap">条/页</span>
            </div>
        </CardFooter>
    )
}
