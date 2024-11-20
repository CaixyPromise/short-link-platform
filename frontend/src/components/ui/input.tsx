import * as React from "react"

import { cn } from "@/lib/utils"
import {Icons} from "@/components/ui/icons";
import {useState} from "react";

export type InputProps = React.InputHTMLAttributes<HTMLInputElement>

const Input = React.forwardRef<HTMLInputElement, InputProps>(
    ({ className, type, ...props }, ref) => {
        return (
            <input
                type={type}
                className={cn(
                    "flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50",
                    className
                )}
                ref={ref}
                {...props}
            />
        )
    }
)
Input.displayName = "Input"

interface CustomInputComponent extends React.ForwardRefExoticComponent<InputProps & React.RefAttributes<HTMLInputElement>> {
    Password: typeof PasswordInput;
    Icon: typeof IconInput;
}
const EnhancedInput = Input as CustomInputComponent;

const PasswordInput = React.forwardRef<HTMLInputElement, InputProps>(
    (props, ref) => {
        const [showPassword, setShowPassword] = useState(false);

        const togglePasswordVisibility = () => {
            setShowPassword(!showPassword);
        };

        return (
            <div className="relative flex items-center">
                <Input
                    {...props}
                    ref={ref}
                    type={showPassword ? 'text' : 'password'}
                    className="pr-10" // 增加padding以避免图标覆盖文字
                />
                <span className="absolute inset-y-0 right-0 flex items-center pr-3">
          <span className="absolute inset-y-0 right-0 flex items-center pr-3 cursor-pointer">
                    {showPassword ? (
                        <Icons.EyeOpen
                            onClick={togglePasswordVisibility}
                            className="hover:text-gray-600 active:text-gray-1000"
                        />
                    ) : (
                        <Icons.EyeClose
                            onClick={togglePasswordVisibility}
                            className="hover:text-gray-600 active:text-gray-1000 active:border-gray-1000"
                        />
                    )}
                </span>
        </span>
            </div>
        );
    }
);

const IconInput: React.FC<{
    prefixIcon: React.ReactNode;
    suffixIcon: React.ReactNode;
    className?: string;
} & InputProps> = ({ prefixIcon, suffixIcon,className, ...inputProps }) => {
    return (
        <div className={`relative flex items-center text-gray-900 ${className}`}>
            {/* Input field with internal prefix and suffix placement */}
            <div className="flex w-full items-center border border-input rounded-md focus-within:ring-1 focus-within:ring-blue-500 focus-within:border-blue-500">
                {prefixIcon && (
                    <div className="flex items-center pl-2 pointer-events-none">
                        {prefixIcon}
                    </div>
                )}
                <input
                    {...inputProps}
                    className="form-input flex-1 px-2 py-2 h-10 text-sm placeholder:text-muted-foreground focus:outline-none"
                />
                {suffixIcon && (
                    <div className="flex items-center pr-2 pointer-events-none">
                        {suffixIcon}
                    </div>
                )}
            </div>
        </div>
    );
};

IconInput.displayName = "IconInput";
PasswordInput.displayName = 'PasswordInput';

EnhancedInput.Icon = IconInput;
EnhancedInput.Password = PasswordInput;

export { EnhancedInput as Input };
