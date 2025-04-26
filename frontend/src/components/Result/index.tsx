import React, { ReactNode } from 'react';
import { CheckCircle2, XCircle } from "lucide-react";
import { Condition, Conditional } from "@/components/Conditional";

interface ResultPageProps {
	status: 'success' | 'failure';
	title: string;
	subText: string;
	extraContent?: ReactNode;
	children?: ReactNode;
}

const Result: React.FC<ResultPageProps> = ({
	                                           status,
	                                           title,
	                                           subText,
	                                           extraContent,
	                                           children,
                                           }) => {
	return (
		<div className="flex flex-col items-center justify-center h-full w-full bg-background p-4">
			<div className="bg-white p-6 rounded-lg w-full max-h-full overflow-auto">
				<div className="text-center w-full">
					<Conditional<string> value={status}>
						<Condition.Switch value={status}>
							<Condition.Case case={"success"}>
								<CheckCircle2 className="w-16 h-16 text-green-500 mx-auto mb-4" />
							</Condition.Case>
							<Condition.Case case={"failure"}>
								<XCircle className="w-16 h-16 text-red-500 mx-auto mb-4" />
							</Condition.Case>
						</Condition.Switch>
					</Conditional>
					<h1 className="text-2xl font-bold text-white mb-3">{title}</h1>
					<p>{subText}</p>
				</div>
				<Conditional value={extraContent}>
					<div className="flex justify-center mt-4 w-full">
						{extraContent}
					</div>
				</Conditional>
				<Conditional value={children}>
					<div className="flex justify-center mt-4 w-full">
						{children}
					</div>
				</Conditional>
			</div>
		</div>
	);
};

export default Result;
