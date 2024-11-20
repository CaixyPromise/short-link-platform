import React from 'react'
import { Loader2 } from 'lucide-react'

interface GlobalContainerProps {
    children: React.ReactNode
    isLoading?: boolean
    loadingRenderer?: React.ReactNode
}

export default function PageContainer({
    children,
    isLoading = false,
    loadingRenderer,
}: GlobalContainerProps) {

    const defaultLoadingRenderer = (
        <div className="flex flex-col items-center justify-center space-y-2">
            <Loader2 className="h-8 w-8 animate-spin" />
            <p className="text-sm text-gray-500 animate-spin">Loading, please wait...</p>
        </div>
    );


    return (
        <div className="container mx-auto px-4 py-4">
            {isLoading ? (
                <div className="flex items-center justify-center h-full">
                    {loadingRenderer || defaultLoadingRenderer}
                </div>
            ) : (
                children
            )}
        </div>
    )
}
