import React from "react";
import { Button } from "@/components/ui/button";
import { CardHeader, CardTitle } from "@/components/ui/card";
import { RefreshCw, Plus, Settings } from "lucide-react";
import {
    DropdownMenu,
    DropdownMenuCheckboxItem,
    DropdownMenuContent,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useDataTable } from "@/components/DataTable/DataTableContext";
import {DefaultOperationButtonConfig, TableActionBarProps} from "@/components/DataTable/types";

export const default_operation_button: DefaultOperationButtonConfig[] = [
    {
        key: 'add',
        label: "添加",
        icon: <Plus className="mr-2 h-4 w-4" />,
        onClick: () => {},
        variant: "default"
    },
    {
        key: 'refresh',
        label: "刷新",
        icon: <RefreshCw className="mr-2 h-4 w-4" />,
        onClick: "refresh",
        variant: "outline"
    },
    {
        key: 'settings',
        label: "列设置",
        icon: <Settings className="mr-2 h-4 w-4" />,
        variant: "outline",
        type: 'columnSettings'
    }
];

export const getTableButtonConfig = (key: string) => {
    return default_operation_button.find(btn => btn.key === key)
}

export const TableActionBar: React.FC<TableActionBarProps> = ({ 
    extraButtons = [], 
    actionButtons = default_operation_button
}) => {
    const { columns, visibleColumns, setVisibleColumns, fetchData, title } = useDataTable();

    const toggleColumn = (columnKey: string) => {
        setVisibleColumns(prev =>
            prev.includes(columnKey)
                ? prev.filter(key => key !== columnKey)
                : [...prev, columnKey]
        );
    };

    const displayColumns = columns?.filter(col => !col.hideInTable);

    const mergedButtons = actionButtons?.map(btn => {
        if (btn?.onClick === "refresh") {
            return { ...btn, onClick: fetchData };
        }
        return btn ?? {};
    });


    const renderButton = (config: DefaultOperationButtonConfig) => {
        const { icon, label, hidden, onClick, variant, enable, type } = config;
        
        if (hidden) return null;

        if (type === 'columnSettings') {
            return (
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button variant={variant} disabled={enable === false}>
                            {icon} {label}
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent>
                        {displayColumns.map((column, index) => (
                            <DropdownMenuCheckboxItem
                                key={index}
                                checked={visibleColumns.includes(column.dataIndex as string)}
                                onCheckedChange={() => toggleColumn(column.dataIndex as string)}
                            >
                                {column.title}
                            </DropdownMenuCheckboxItem>
                        ))}
                    </DropdownMenuContent>
                </DropdownMenu>
            );
        }

        return (
            <Button variant={variant} onClick={onClick as () => void} disabled={enable === false}>
                {icon} {label}
            </Button>
        );
    };

    return (
        <CardHeader>
            <div className="flex justify-between items-center">
                <CardTitle>{title || "数据列表"}</CardTitle>
                <div className="space-x-2">
                    {mergedButtons.map((button, index) => (
                        <React.Fragment key={button.key || index}>
                            {renderButton(button)}
                        </React.Fragment>
                    ))}
                    {extraButtons.map((button, index) => (
                        <React.Fragment key={index}>{button}</React.Fragment>
                    ))}
                </div>
            </div>
        </CardHeader>
    );
};
