"use client"

import React, { useRef, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Eye, EyeOff, RefreshCw, Copy, Download } from "lucide-react"
import { useToast } from "@/hooks/use-toast"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"

/**
 * 1. 封装 APIKeyItem 组件
 */
type ApiKeyItemProps = {
	value: string;
	onCopy: (value: string, inputRef: React.RefObject<HTMLInputElement>) => void;
	label?: string;  // 如果需要给每个 key 添加标题/label，可在此使用
};

const ApiKeyItem: React.FC<ApiKeyItemProps> = ({ value, onCopy, label }) => {
	const [isShow, setIsShow] = useState(false)
	const inputRef = useRef<HTMLInputElement>(null)

	const handleToggle = () => setIsShow(!isShow)
	const handleCopyClick = () => onCopy(value, inputRef)

	return (
		<div className="flex items-center space-x-2">
			{/* 如果需要标签或标题，可以解开下面注释 */}
			{/* <span className="w-16 text-right">{label}</span> */}
			<Input
				ref={inputRef}
				type={isShow ? "text" : "password"}
				value={value}
				readOnly
			/>
			<Button variant="outline" onClick={handleToggle}>
				{isShow ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
			</Button>
			<Button variant="outline" onClick={handleCopyClick}>
				<Copy className="h-4 w-4" />
			</Button>
		</div>
	)
}

/**
 * 2. 封装 SDKs Tabs 组件
 */
type SdkInfo = {
	name: string;
	importCode: string;
	usageCode: string;
	repoLink: string;
};

type SdkTabsProps = {
	sdks: SdkInfo[];
};

const SdkTabs: React.FC<SdkTabsProps> = ({ sdks }) => {
	// 先取第一个 SDK 作为默认 tab
	const defaultValue = sdks[0]?.name.toLowerCase() || ""

	return (
		<Tabs defaultValue={sdks[0]?.name.toLowerCase() || ""}>
			<TabsList>
				{sdks.map((sdk) => (
					<React.Fragment key={sdk.name}>
						<TabsTrigger value={sdk.name.toLowerCase()}>{sdk.name}</TabsTrigger>
					</React.Fragment>
				))}
			</TabsList>
			{sdks.map((sdk) => (
				<TabsContent key={sdk.name} value={sdk.name.toLowerCase()} className="space-y-4">
					<Button>
						<Download className="mr-2 h-4 w-4" /> Download {sdk.name} SDK
					</Button>
					<div>
						<h4 className="text-sm font-medium">How to import:</h4>
						<pre className="bg-muted p-2 rounded-md">
          <code>{sdk.importCode}</code>
        </pre>
					</div>
					<div>
						<h4 className="text-sm font-medium">How to use:</h4>
						<pre className="bg-muted p-2 rounded-md">
          <code>{sdk.usageCode}</code>
        </pre>
					</div>
					<div>
						<h4 className="text-sm font-medium">Repository:</h4>
						<a
							href={sdk.repoLink}
							className="text-blue-500 hover:underline"
							target="_blank"
							rel="noreferrer"
						>
							{sdk.repoLink}
						</a>
					</div>
				</TabsContent>
			))}
		</Tabs>

	)
}

export default function ApiManagement() {
	const { toast } = useToast()

	// 三个 key
	const [apiKey, setApiKey] = useState("your-api-key-here")
	const [sk, setSK] = useState("your-sk-here")
	const [ak, setAK] = useState("your-ak-here")

	// 复制逻辑
	const handleCopy = async (value: string, inputRef: React.RefObject<HTMLInputElement>) => {
		try {
			await navigator.clipboard.writeText(value)
			toast({
				title: "复制成功",
				description: "已复制到剪贴板",
			})
		} catch {
			// 如果复制失败
			toast({
				title: "复制失败",
				description: "请手动复制",
				variant: "destructive",
			})
			if (inputRef.current) {
				inputRef.current.type = "text"
				inputRef.current.focus()
				inputRef.current.select()
			}
		}
	}

	// 刷新 keys，按钮只要一个即可全部刷新
	const refreshKeys = () => {
		setApiKey("new-api-key-" + Math.random().toString(36).substring(7))
		setSK("new-sk-" + Math.random().toString(36).substring(7))
		setAK("new-ak-" + Math.random().toString(36).substring(7))
	}

	// SDK 配置信息
	const sdkList: SdkInfo[] = [
		{
			name: "Java",
			importCode: "import com.example.api.SDK;",
			usageCode: `SDK sdk = new SDK("your-api-key");
sdk.makeRequest();`,
			repoLink: "https://github.com/example/java-sdk",
		},
		{
			name: "Python",
			importCode: "from example_api import SDK",
			usageCode: `sdk = SDK("your-api-key")
sdk.make_request()`,
			repoLink: "https://github.com/example/python-sdk",
		},
		{
			name: "C++",
			importCode: '#include "example_api.h"',
			usageCode: `ExampleAPI::SDK sdk("your-api-key");
sdk.makeRequest();`,
			repoLink: "https://github.com/example/cpp-sdk",
		},
	]

	return (
		<div className="space-y-8">
			{/* 1. API Keys */}
			<Card>
				<CardHeader>
					<CardTitle>API Keys</CardTitle>
					<CardDescription>Manage your API keys here. Keep these secret!</CardDescription>
				</CardHeader>
				<CardContent className="space-y-4">
					{/* 使用封装的 ApiKeyItem */}
					<ApiKeyItem value={apiKey} onCopy={handleCopy} />
					<ApiKeyItem value={sk} onCopy={handleCopy} />
					<ApiKeyItem value={ak} onCopy={handleCopy} />

					<Button onClick={refreshKeys}>
						<RefreshCw className="mr-2 h-4 w-4" /> Refresh Keys
					</Button>
				</CardContent>
			</Card>

			{/* 2. API SDKs */}
			<Card>
				<CardHeader>
					<CardTitle>API SDKs</CardTitle>
					<CardDescription>Download and learn how to use our SDKs</CardDescription>
				</CardHeader>
				<CardContent>
					<SdkTabs sdks={sdkList} />
				</CardContent>
			</Card>
		</div>
	)
}
