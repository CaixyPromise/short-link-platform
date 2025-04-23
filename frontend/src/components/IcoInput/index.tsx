import * as React from "react";
import {InputProps} from "@/components/ui/input";

const IconInput: React.FC<{
	prefixIcon: React.ReactNode;
	suffixIcon: React.ReactNode;
	className?: string;
} & InputProps> = ({prefixIcon, suffixIcon, className, ...inputProps}) => {
	return (
		<div className={`relative flex items-center text-gray-900 ${className}`}>
			{/* Input field with internal prefix and suffix placement */}
			<div
				className="flex w-full items-center border border-input rounded-md focus-within:ring-1 focus-within:ring-blue-500 focus-within:border-blue-500">
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

export default IconInput;