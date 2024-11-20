import exp from "node:constants";
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {copyToClipboard} from "@/lib/copyToClipboard";
import {Copy} from "lucide-react";
import React from "react";

interface CopyableTextProps {
    text: string;
    className?: string
}

const CopyableText = ({text, className}: CopyableTextProps) => {
    return (
        <div className={`flex items-center ${className}`}>
            <span className="ml-1">{text}</span>
            <span className="ml-2 cursor-pointer hover:text-blue-300">
                <Popover>
                    <PopoverTrigger
                        asChild
                        onClick={() => {
                            copyToClipboard(text)
                        }}>
                        <Copy className="h-4 w-4"/>
                    </PopoverTrigger>
                    <PopoverContent>
                        {`已复制: ${text}`}
                    </PopoverContent>
                </Popover>
            </span>
        </div>
    )
}

export default CopyableText;