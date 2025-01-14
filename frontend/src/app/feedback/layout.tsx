import {DialogProvider} from "@/components/Result/contexts/ModalContext";

export default function FeedbackPage({children}: {children: React.ReactNode}) {
	return (
		<DialogProvider>
			<div className="container mx-auto py-10">
				<h1 className="text-3xl font-bold mb-6">反馈</h1>
				<p className="text-gray-600 mb-8">
					我们非常重视您的意见。请填写以下表单，帮助我们改进我们的短链服务。
				</p>
				{children}
			</div>
		</DialogProvider>
	)
}

