import React from 'react'
import { cn } from "@/lib/utils"
import { Inbox } from 'lucide-react'

interface EmptyIconProps extends React.HTMLAttributes<HTMLDivElement> {}

const EmptyIcon = React.forwardRef<HTMLDivElement, EmptyIconProps>(
    ({ className, children, ...props }, ref) => {
        return (
            <div
                ref={ref}
                className={cn(
                    "flex items-center justify-center rounded-full bg-muted",
                    className
                )}
                {...props}
            >
                {children || <Inbox className="w-10 h-10 text-muted-foreground" />}
            </div>
        )
    }
)
EmptyIcon.displayName = "EmptyIcon"

export { EmptyIcon }

