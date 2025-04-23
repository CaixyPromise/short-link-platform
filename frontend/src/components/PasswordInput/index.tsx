"use client";

import React, {useState} from "react";
import {Eye, EyeOff} from "lucide-react";
import {Input, InputProps} from "@/components/ui/input";

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
	            <Eye
		            onClick={togglePasswordVisibility}
		            className="hover:text-gray-600 active:text-gray-1000"
	            />
            ) : (
	            <EyeOff
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

export default PasswordInput;