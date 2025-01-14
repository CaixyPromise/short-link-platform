import {Separator} from "@/components/ui/separator";
import React from "react";

const SettingPageWrapper: React.FC<{
	children: React.ReactNode
	title: string;
	description: string
}> = ({title, description, children}) => {
	return (
		<div className="space-y-6">
			<div>
				<h3 className="text-lg font-medium">{title}</h3>
				<p className="text-sm text-muted-foreground">
					{description}
				</p>
			</div>
			<Separator/>
			{children}
		</div>
	)
}
export default SettingPageWrapper;