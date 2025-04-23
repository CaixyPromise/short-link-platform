import type { ReactNode } from "react"

interface FormContainerProps {
	children: ReactNode
}

export function FormContainer({ children }: FormContainerProps) {
	return (
		<div className="w-full max-w-md p-8 rounded-xl backdrop-blur-md bg-white/20 border border-white/30 shadow-xl">
			{children}
		</div>
	)
}
