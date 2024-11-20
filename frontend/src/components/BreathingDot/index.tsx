import React from "react";

export type BreathingDotStatus = "success" | "error" | "warning" | "default";

interface BreathingDotProps {
    className?: string;
    status: BreathingDotStatus
}

const BreathingDot: React.FC<BreathingDotProps> = ({className, status}) => {
    if (!status) return null;

    const statusColorMap: Record<"success" | "error" | "warning" | "default", string> = {
        success: "bg-green-500",
        error: "bg-red-500",
        warning: "bg-yellow-500",
        default: "bg-gray-400",
    };

    return (
        <span
            className={`w-2 h-2 rounded-full ${statusColorMap[status]} animate-pulse inline-block ${className}`}
            title={status}
        ></span>
    );
}
export default BreathingDot;