import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {copyToClipboard} from "@/lib/copyToClipboard";
import {Copy} from "lucide-react";
import React from "react";

interface CopyableTextProps {
    text: string;
    className?: string;
    type?: "link" | "text" | "email";
}

const CopyableText = ({text, className, type}: CopyableTextProps) => {
    if (!text) {
        return null;
    }
    const renderText = () => {
        const baseClass = "text-black hover:underline hover:text-blue-600 transition-colors";
        if (type === "link") {
            return (
                <a href={text} target="_blank" rel="noopener noreferrer" className={baseClass}>
                    {text}
                </a>
            );
        }
        if (type === "email") {
            return (
                <a href={`mailto:${text}`} className={baseClass}>
                    {text}
                </a>
            );
        }
        return <span className="text-black">{text}</span>;
    };
    return (
        <div className={`flex items-center gap-1 ${className}`}>
            <span className="ml-1">{renderText()}</span>
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