import React from 'react';
import {Control, Controller, RegisterOptions} from 'react-hook-form';
import {Tooltip, TooltipContent, TooltipTrigger} from '@/components/ui/tooltip';
interface EnhancedControllerProps {
    control: Control;
    name: string;
    rules?: Omit<RegisterOptions, 'valueAsNumber' | 'valueAsDate' | 'setValueAs' | 'disabled'>;
    children: React.ReactNode;
}
const EnhancedController:React.FC<EnhancedControllerProps> = ({ control, name, rules, children }) => {
    return (
        <Controller
            name={name}
            control={control}
            rules={rules}
            render={({ field, fieldState: { error } }) => (
                <>
                    <Tooltip>
                        <TooltipTrigger asChild>
                            {React.cloneElement(children, {
                                ...field,
                                // isError: !!error,  // 可用于显示错误状态
                            })}
                        </TooltipTrigger>
                        {error && (
                            <TooltipContent
                                style={{
                                    backgroundColor: 'rgba(255, 100, 100, 0.9)',
                                    maxWidth: '230px',
                                    whiteSpace: 'normal' }}
                                side="right"
                            >
                                <p className="text-red-500 text-sm" style={{ color: '#fff' }}>{error.message}</p>
                            </TooltipContent>
                        )}
                    </Tooltip>
                </>
            )}
        />
    );
};

export default EnhancedController;
