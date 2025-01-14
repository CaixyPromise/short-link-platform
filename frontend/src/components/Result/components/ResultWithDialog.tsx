import React from 'react';
import {Dialog, DialogContent} from "@/components/ui/dialog"
import Result from "@/components/Result";


const ResultWithDialog: React.FC<{
	isOpen: boolean;
	closeDialog: () => void;
	resultParams: any;
}> = ({isOpen, closeDialog, resultParams}) => {

	if (!resultParams) return null;

	return (
		<Dialog open={isOpen} onOpenChange={(open) => !open && closeDialog()} modal={resultParams?.modal}>
			<DialogContent
				className="max-h-[80vh] overflow-y-auto" // 限制最大高度和设置滚动
			>
				<Result {...resultParams} />
			</DialogContent>
		</Dialog>
	);
};

export default ResultWithDialog;

