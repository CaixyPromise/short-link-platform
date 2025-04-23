import { Button } from "@/components/ui/button"
import { Github, Twitter, ChromeIcon as Google } from "lucide-react"

const oauthProviders = [
	{ name: "GitHub", icon: Github, color: "bg-gray-800" },
	{ name: "Twitter", icon: Twitter, color: "bg-blue-400" },
	{ name: "Google", icon: Google, color: "bg-red-500" },
]

export function OAuthOptions() {
	return (
		<div className="mt-6">
			<div className="relative">
				<div className="absolute inset-0 flex items-center">
					<span className="w-full border-t border-gray-300" />
				</div>
				<div className="relative flex justify-center text-sm">
					<span className="px-2 bg-white/20 backdrop-blur-sm text-gray-600">或使用以下第三方登录</span>
				</div>
			</div>

			<div className="mt-6 grid grid-cols-3 gap-3">
				{oauthProviders.map((provider) => (
					<Button
						key={provider.name}
						variant="outline"
						className={`w-full ${provider.color} text-white hover:bg-opacity-80`}
					>
						<provider.icon className="w-5 h-5" />
						<span className="sr-only">{provider.name}</span>
					</Button>
				))}
			</div>
		</div>
	)
}
