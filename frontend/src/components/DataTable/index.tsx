// DataTable/index.tsx
"use client"

import React from "react"
import { DataTableProvider } from "./DataTableContext"

import { DataTableProps } from "./types"
import {SearchAreaBar} from "@/components/DataTable/components/SearchAreaBar";
import {TableActionBar} from "@/components/DataTable/components/TableActionBar";
import {TableContent} from "@/components/DataTable/components/TableContent";
import {TableFooter} from "@/components/DataTable/components/TableFooter";
import {Card} from "@/components/ui/card";
import {TooltipProvider} from "@/components/ui/tooltip";

const DataTable = <T extends Record<string, any>>({
    columns,
    request,
    dataSource,
    title,
    initialPagination,
    components,
    className,
    style,
}: DataTableProps<T>) => {
    return (
        <DataTableProvider
            columns={columns}
            request={request}
            dataSource={dataSource}
            title={title}
            initialPagination={initialPagination}
        >
            <div className={className} style={style}>
                {components?.SearchArea !== null && (components?.SearchArea || <SearchAreaBar  />)}
                <div className="mt-4">
                    <Card>
                        <TooltipProvider>
                            {components?.TableActionBar !== null && (components?.TableActionBar || <TableActionBar/>)}
                            {components?.TableContent !== null && (components?.TableContent || <TableContent />)}
                            {components?.TableFooter !== null && (components?.TableFooter || <TableFooter />)}
                        </TooltipProvider>
                    </Card>
                </div>
            </div>
        </DataTableProvider>
    )
}

export default DataTable
