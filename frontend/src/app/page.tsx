"use client"
import {Plus, Link2, FileText, Rocket, ArrowRight, BookOpen, Code, Users, CirclePlus} from "lucide-react"
import {motion} from "framer-motion"

import {Button} from "@/components/ui/button"
import {Card, CardContent, CardHeader, CardTitle, CardDescription} from "@/components/ui/card"
import {Condition, Conditional} from "@/components/Conditional";
import {useAppDispatch, useAppSelector} from "@/stores/hooks";
import {updateAddGroupModalVisible} from "@/stores/Group";
import {useRouter} from "next/navigation";

export default function Dashboard() {
	const {groupList } = useAppSelector(state => state.Group);
	const {title} = useAppSelector(state => state.SystemConfig)
	const router = useRouter();
	const dispatch = useAppDispatch();
	const setAddGroupModalVisible = (state: boolean) => {
		dispatch(updateAddGroupModalVisible(state))
	}

	const toFirstGroup = () => {
		if (groupList?.length > 0) {
			const firstItem = groupList?.at(0);
			if (firstItem?.gid) {
				router.push(`/link/${firstItem.gid}`)
			}
		}
	}

	return (
		<Conditional value={groupList} clientOnly>
			<motion.div
				className="flex flex-col gap-6 p-6"
				initial={{opacity: 0}}
				animate={{opacity: 1}}
				transition={{duration: 0.5}}
			>
				{/* Welcome Section */}
				<section className="space-y-6">
					<div className="space-y-2">
						<motion.h1
							className="text-2xl font-semibold tracking-tight"
							initial={{y: -20, opacity: 0}}
							animate={{y: 0, opacity: 1}}
							transition={{duration: 0.8}}
						>
							欢迎使用 {title}
						</motion.h1>
					</div>

					<Card>
						<CardHeader>
							<CardTitle>快速开始</CardTitle>
							<CardDescription>完成以下步骤，开启您的短链接之旅</CardDescription>
						</CardHeader>
						<CardContent className="grid gap-4">
							<motion.div
								className="flex items-center gap-4 rounded-lg border p-4"
								initial={{opacity: 0, x: -50}}
								animate={{opacity: 1, x: 0}}
								transition={{duration: 0.5}}
							>
								<div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-primary/10">
									<Users className="h-6 w-6 text-primary"/>
								</div>
								<div className="flex-1 space-y-1">
									<p className="font-medium">创建分组</p>
									<p className="text-sm text-muted-foreground">创建您的第一个链接分组，更好地管理您的短链接</p>
								</div>
								<Button
									onClick={() => setAddGroupModalVisible(true)}
								>
									立即创建
									<ArrowRight className="ml-2 h-4 w-4"/>
								</Button>
							</motion.div>

							<Condition.When test={groupList?.length > 0}>
								<motion.div
									className="flex items-center gap-4 rounded-lg border p-4"
									initial={{opacity: 0, x: 50}}
									animate={{opacity: 1, x: 0}}
									transition={{duration: 0.5}}
								>
									<div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-primary/10">
										<Link2 className="h-6 w-6 text-primary"/>
									</div>
									<div className="flex-1 space-y-1">
										<p className="font-medium">管理生成短链接</p>
										<p className="text-sm text-muted-foreground">去管理你的锻炼，随时体验便捷的链接管理</p>
									</div>
									<Button onClick={toFirstGroup}>
										开始生成
										<ArrowRight className="ml-2 h-4 w-4"/>
									</Button>
								</motion.div>
							</Condition.When>

						</CardContent>
					</Card>
				</section>

				{/* Features Grid */}
				<section className="grid gap-4 md:grid-cols-2">
					{/* 功能特点部分 */}
					<motion.div
						initial={{opacity: 0, y: 20}}
						animate={{opacity: 1, y: 0}}
						transition={{duration: 0.5, delay: 0.2}}
					>
						<Card className="flex flex-col min-h-full">
							<CardHeader>
								<CardTitle className="flex items-center gap-2">
									<Rocket className="h-6 w-6 text-primary"/>
									功能特点
								</CardTitle>
							</CardHeader>
							<CardContent className="flex-1">
								{/* 使用 grid 布局，2 列自适应多行 */}
								<div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
									{/* 功能点项 */}
									<motion.div
										className="flex items-center gap-3 hover:bg-primary/5 rounded-lg p-2 transition-colors"
										whileHover={{scale: 1.05}}
										whileTap={{scale: 0.98}}
									>
										<Link2 className="h-6 w-6 text-primary"/>
										<span className="text-sm">自定义短链接</span>
									</motion.div>

									<motion.div
										className="flex items-center gap-3 hover:bg-primary/5 rounded-lg p-2 transition-colors"
										whileHover={{scale: 1.05}}
										whileTap={{scale: 0.98}}
									>
										<BookOpen className="h-6 w-6 text-primary"/>
										<span className="text-sm">访问统计分析</span>
									</motion.div>

									<motion.div
										className="flex items-center gap-3 hover:bg-primary/5 rounded-lg p-2 transition-colors"
										whileHover={{scale: 1.05}}
										whileTap={{scale: 0.98}}
									>
										<FileText className="h-6 w-6 text-primary"/>
										<span className="text-sm">批量链接管理</span>
									</motion.div>

									<motion.div
										className="flex items-center gap-3 hover:bg-primary/5 rounded-lg p-2 transition-colors"
										whileHover={{scale: 1.05}}
										whileTap={{scale: 0.98}}
									>
										<Code className="h-6 w-6 text-primary"/>
										<span className="text-sm">地域分布追踪</span>
									</motion.div>
								</div>
							</CardContent>
						</Card>
					</motion.div>

					<motion.div
						initial={{opacity: 0, y: 20}}
						animate={{opacity: 1, y: 0}}
						transition={{duration: 0.5, delay: 0.6}}
					>
						<Card className="flex flex-col min-h-full">
							<CardHeader>
								<CardTitle className="flex items-center gap-2">
									<Code className="h-6 w-6 text-primary"/>
									API 集成
								</CardTitle>
							</CardHeader>
							<CardContent className="space-y-4 flex-1">
								<p className="text-sm text-muted-foreground">
									通过API实现自动化操作，将短链服务集成到您的系统中。
								</p>
								<Button variant="outline" className="w-full">
									查看API文档
								</Button>
							</CardContent>
						</Card>
					</motion.div>
				</section>

				{/* Quick Actions */}
				<motion.section
					initial={{opacity: 0, y: 20}}
					animate={{opacity: 1, y: 0}}
					transition={{duration: 0.5}}
				>
					<Card>
						<CardHeader>
							<CardTitle>快捷操作</CardTitle>
							<CardDescription>常用功能快速访问</CardDescription>
						</CardHeader>
						<CardContent>
							{/* 使用 flex 布局，确保按钮平分宽度 */}
							<div className="flex flex-wrap gap-4">
								<Condition.When test={groupList?.length > 0}>
									<motion.div whileHover={{scale: 1.1}} whileTap={{scale: 0.95}} className="flex-1">
										<Button
											variant="outline"
											className="w-full h-24 flex flex-col space-y-2 hover:bg-primary/5 transition-colors"
											onClick={toFirstGroup}
										>
											<motion.div
												initial={{scale: 1}}
												animate={{scale: [1, 1.2, 1]}}
												transition={{duration: 1, repeat: Number.POSITIVE_INFINITY, repeatDelay: 2}}
											>
												<Plus className="h-6 w-6"/>
											</motion.div>
											<span>管理短链接</span>
										</Button>
									</motion.div>
								</Condition.When>

								<motion.div whileHover={{scale: 1.1}} whileTap={{scale: 0.95}} className="flex-1">
									<Button
										variant="outline"
										className="w-full h-24 flex flex-col space-y-2 hover:bg-primary/5 transition-colors"
										onClick={()=>setAddGroupModalVisible(true)}
									>
										<motion.div
											initial={{scale: 1}}
											animate={{scale: [1, 1.2, 1]}}
											transition={{duration: 1, repeat: Number.POSITIVE_INFINITY, repeatDelay: 2}}
										>
											<CirclePlus className="h-6 w-6"/>
										</motion.div>
										<span>新建分组</span>
									</Button>
								</motion.div>

								<motion.div whileHover={{scale: 1.1}} whileTap={{scale: 0.95}} className="flex-1">
									<Button
										variant="outline"
										className="w-full h-24 flex flex-col space-y-2 hover:bg-primary/5 transition-colors"
										onClick={()=>{
											router.push('/settings/api-management')
										}}
									>
										<motion.div animate={{rotateY: 360}} transition={{duration: 2, repeat: Number.POSITIVE_INFINITY}}>
											<Code className="h-6 w-6"/>
										</motion.div>
										<span>API 密钥</span>
									</Button>
								</motion.div>
							</div>
						</CardContent>
					</Card>
				</motion.section>
			</motion.div>
		</Conditional>
	)
}
