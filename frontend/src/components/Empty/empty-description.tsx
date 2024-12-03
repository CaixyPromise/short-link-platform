import React from 'react'
import { cn } from "@/lib/utils"

interface EmptyDescriptionProps extends React.HTMLAttributes<HTMLParagraphElement> {}

const EmptyDescription = React.forwardRef<HTMLParagraphElement, EmptyDescriptionProps>(
    ({ className, ...props }, ref) => {
        return (
            <p
                ref={ref}
                className={cn("mt-4 text-sm text-muted-foreground", className)}
                {...props}
            />
        )
    }
)
EmptyDescription.displayName = "EmptyDescription"

export { EmptyDescription }

