import React from 'react'
import { cn } from "@/lib/utils"
import {EmptyIcon} from "@/components/Empty/empty-icon";
import {EmptyDescription} from "@/components/Empty/empty-description";

interface EmptyProps extends React.HTMLAttributes<HTMLDivElement> {
    description?: string
}

const Empty = React.forwardRef<HTMLDivElement, EmptyProps>(
    ({ className, children, description, ...props }, ref) => {
        return (
            <div
                ref={ref}
                className={cn(
                    "flex flex-col items-center justify-center h-full w-full p-8 text-center",
                    className
                )}
                {...props}
            >
                {children ? (
                    children
                ) : (
                    <>
                        <EmptyIcon />
                        {description && <EmptyDescription>{description}</EmptyDescription>}
                    </>
                )}
            </div>
        )
    }
)
Empty.displayName = "Empty"

export { Empty }

