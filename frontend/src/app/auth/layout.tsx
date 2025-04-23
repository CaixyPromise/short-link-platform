import {AuthPageProvider} from "@/app/auth/contexts";
import React from "react";

const AuthPageLayout: React.FC<{children: React.ReactNode}> = ({children}) => {

	return (
		<div className="min-h-screen overflow-hidden bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50">
			{/* Background Image */}
			<div
				className="fixed inset-0 bg-cover bg-center opacity-30"
				style={{
					backgroundImage: "url('/placeholder.svg?height=1080&width=1920')",
					backgroundBlendMode: "overlay",
				}}
			/>

			{/* Abstract Shapes */}
			<div className="fixed inset-0 overflow-hidden">
				<div
					className="absolute -top-40 -left-40 w-80 h-80 bg-purple-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob"></div>
				<div
					className="absolute top-0 -right-20 w-80 h-80 bg-blue-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-2000"></div>
				<div
					className="absolute -bottom-40 left-20 w-80 h-80 bg-indigo-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-4000"></div>
			</div>
			<AuthPageProvider>
				{children}
			</AuthPageProvider>
		</div>
	)
}

export default AuthPageLayout;