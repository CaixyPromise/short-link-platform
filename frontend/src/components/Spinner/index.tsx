import React from 'react';
import { Icon } from "@/components/ui/icons";
import { Conditional } from "@/components/Conditional";
import { cn } from "@/lib/utils";

interface SpinProps {
    children: React.ReactNode;
    loading: boolean;
    loadingText?: string;
    customLoadingIndicator?: React.ReactNode;
}

const SpinPage: React.FC<SpinProps> = ({
                                           children,
                                           loading,
                                           loadingText,
                                           customLoadingIndicator
                                       }) => {
    return (
        <div className="inline-block w-full">
            <div className="relative">
                <Conditional value={loading}>
                    <div className="absolute inset-0 flex flex-col justify-center items-center bg-white/70 z-20">
                        {customLoadingIndicator || (
                            <Icon icon="Spinner" className="animate-spin h-8 w-8 text-primary" />
                        )}
                        {loadingText && (
                            <span className={cn(
                                "mt-2 text-sm font-medium text-primary",
                                "animate-pulse"
                            )}>
                {loadingText}
              </span>
                        )}
                    </div>
                </Conditional>
                <div className={loading ? 'opacity-50' : 'opacity-100'}>
                    {children}
                </div>
            </div>
        </div>
    );
};

export default SpinPage;

