import React from 'react';
import {Icon} from "@/components/ui/icons";
import {Conditional} from "@/components/Conditional";

interface SpinProps
{
    children: React.ReactNode
    loading: boolean
}

const SpinPage: React.FC<SpinProps> = ({children, loading}) =>
{
    return (
        <div className="inline-block w-full">
            <div className="relative">
                <Conditional value={loading}>
                    <div className="absolute inset-0 flex justify-center items-center bg-white/70 z-10">
                        <Icon icon="Spinner" className="animate-spin"/>
                    </div>
                </Conditional>
                <div className={loading ? 'opacity-50' : 'opacity-100'}>
                    {children}
                </div>
            </div>
        </div>
    )
}

export default SpinPage;
