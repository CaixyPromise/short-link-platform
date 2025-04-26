"use client"
import React, {useCallback, useEffect, useState} from 'react'
import {Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger} from "@/components/ui/dialog"
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs"
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import ReactECharts from 'echarts-for-react'
import {useLinkStatsModal} from "@/components/LinkStatsModal/context";
import {shortLinkStats} from "@/api/linkAccessStatsController";
import {ResultCode} from "@/enums/ResultCodeEnum";
import {useToast} from "@/hooks/use-toast";
import {startOfDay, endOfDay, subDays, format} from 'date-fns';
import Spinner from "@/components/Spinner";
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {Calendar} from "@/components/ui/calendar";
import {Button} from "@/components/ui/button";

const getDateRange = (tab: string): { startDate: Date; endDate: Date } => {
	const now = new Date()
	switch (tab) {
		case 'today':
			return {startDate: startOfDay(now), endDate: endOfDay(now)}
		case 'yesterday':
			const yesterday = subDays(now, 1)
			return {startDate: startOfDay(yesterday), endDate: endOfDay(yesterday)}
		case '7days':
			return {startDate: startOfDay(subDays(now, 6)), endDate: endOfDay(now)}
		case '30days':
			return {startDate: startOfDay(subDays(now, 29)), endDate: endOfDay(now)}
		default:
			return {startDate: startOfDay(now), endDate: endOfDay(now)}
	}
}

export default function ShortLinkStats() {
	const {statsModalVisible, setStatsModalVisible, linkItem} = useLinkStatsModal();
	const [statsData, setStatsData] = useState<API.ShortLinkStatsRespDTO>({});
	const [activeTab, setActiveTab] = useState('today')
	const [dateRange, setDateRange] = useState(getDateRange('today'))
	const {toast} = useToast();
	const [loading, setLoading] = useState<boolean>(false);
	const [customDateRange, setCustomDateRange] = useState<{ from: Date; to: Date } | undefined>(undefined)
	const [isCustomDatePopoverOpen, setIsCustomDatePopoverOpen] = useState(false)

	// 处理 Tabs 切换
	const handleTabChange = (value: string) => {
		setActiveTab(value)
		// 如果切换到非 custom，就清空自定义区间
		if (value !== 'custom') {
			setCustomDateRange(undefined)
		}
	}

	// 发起请求
	const queryLinkStats = useCallback((range: { startDate: Date; endDate: Date }) => {
		setLoading(true);
		shortLinkStats({
			requestParam: {
				fullShortUrl: linkItem.fullShortUrl,
				gid: linkItem.gid,
				startDate: format(range.startDate, 'yyyy-MM-dd'),
				endDate: format(range.endDate, 'yyyy-MM-dd'),
				enableStatus: 0,
			},
		}).then((res) => {
			const {code, data} = res;
			if (code === ResultCode.SUCCESS) {
				setStatsData(data);
			}
		}).catch((err) => {
			toast({
				title: "请求数据失败",
				description: err.message,
				variant: "destructive",
			});
		}).finally(() => {
			setLoading(false);
		});
	}, [linkItem.fullShortUrl, linkItem.gid, toast])

	/**
	 * 当弹窗打开并且 linkItem 有值时，根据 activeTab 判断要不要走自定义时间。
	 *  - 如果是内置 tab，调用 getDateRange
	 *  - 如果是 custom，则从 customDateRange 里取值
	 */
	useEffect(() => {
		if (statsModalVisible && linkItem?.id) {
			if (activeTab === 'custom' && customDateRange?.from && customDateRange?.to) {
				const range = {
					startDate: startOfDay(customDateRange.from),
					endDate: endOfDay(customDateRange.to),
				}
				setDateRange(range)
				queryLinkStats(range)
			} else if (activeTab !== 'custom') {
				const range = getDateRange(activeTab)
				setDateRange(range)
				queryLinkStats(range)
			}
		}
	}, [statsModalVisible, linkItem, activeTab, customDateRange, queryLinkStats])

	const overviewOption = {
		title: {text: '访问概览'},
		tooltip: {trigger: 'axis'},
		legend: {data: ['PV', 'UV', 'UIP']},
		xAxis: {type: 'category', data: statsData?.daily?.map(d => d.date) || []},
		yAxis: {type: 'value'},
		series: [
			{
				name: 'PV',
				type: ['today', 'yesterday'].includes(activeTab) ? 'bar' : 'line',
				data: statsData?.daily?.map(d => d.pv) || []
			},
			{
				name: 'UV',
				type: ['today', 'yesterday'].includes(activeTab) ? 'bar' : 'line',
				data: statsData?.daily?.map(d => d.uv) || []
			},
			{
				name: 'UIP',
				type: ['today', 'yesterday'].includes(activeTab) ? 'bar' : 'line',
				data: statsData?.daily?.map(d => d.uip) || []
			},
		]
	}

	const localeOption = {
		title: {text: '访问地理位置'},
		tooltip: {trigger: 'item'},
		series: [
			{
				type: 'pie',
				data: statsData?.localeCnStats?.map(d => ({
					value: d.cnt,
					name: d.locale
				})) || [],
			}
		]
	}

	const hourlyOption = {
		title: {text: '小时访问量'},
		tooltip: {trigger: 'axis'},
		xAxis: {type: 'category', data: Array.from({length: 24}, (_, i) => i)},
		yAxis: {type: 'value'},
		series: [{type: 'bar', data: statsData?.hourStats}]
	}

	return (
		<Dialog open={statsModalVisible} onOpenChange={setStatsModalVisible} modal={false}>
			<DialogContent
				className="max-w-[80vw] max-h-[80vh] overflow-y-auto data-[state=open]:animate-none"
			>
				<DialogHeader>
					<DialogTitle>短链访问数据 - {linkItem?.linkName ?? ""}</DialogTitle>
				</DialogHeader>
				<Spinner loading={loading}>
					<Tabs value={activeTab} onValueChange={handleTabChange} className="w-full">
						<TabsList>
							<TabsTrigger value="today">今日</TabsTrigger>
							<TabsTrigger value="yesterday">昨日</TabsTrigger>
							<TabsTrigger value="7days">过去一周</TabsTrigger>
							<TabsTrigger value="30days">过去一个月</TabsTrigger>
							<Popover
								open={isCustomDatePopoverOpen}
								onOpenChange={setIsCustomDatePopoverOpen}
							>
								<PopoverTrigger asChild>
									<TabsTrigger
										value="custom"
										className={`relative ${activeTab === "custom" ? "bg-blue-100 text-blue-600 " : ""}`}
									>
										自定义范围
										{customDateRange?.to && (
											<span className="ml-1 text-xs text-gray-500">
                                                ({customDateRange.from.toLocaleDateString()} - {customDateRange.to.toLocaleDateString()})
                                            </span>
										)}
									</TabsTrigger>
								</PopoverTrigger>

								<PopoverContent
									className="w-auto p-0 animate-none"
									align="start"
									side="right"
								>
									<Calendar
										mode="range"
										selected={customDateRange}
										onSelect={setCustomDateRange}
										numberOfMonths={2}
									/>
								</PopoverContent>
							</Popover>
						</TabsList>
						<TabsContent value={activeTab} className="space-y-4">
							<Card>
								<CardHeader>
									<CardTitle>Overview</CardTitle>
									<CardDescription>
										时间范围{" "}
										{dateRange.startDate.toLocaleDateString()} - {dateRange.endDate.toLocaleDateString()}
									</CardDescription>
								</CardHeader>
								<CardContent>
									<ReactECharts option={overviewOption} style={{height: '300px'}}/>
								</CardContent>
							</Card>
							<div className="grid grid-cols-2 gap-4">
								<Card>
									<CardHeader>
										<CardTitle>访问地理位置</CardTitle>
									</CardHeader>
									<CardContent>
										<ReactECharts option={localeOption} style={{height: '300px'}}/>
									</CardContent>
								</Card>
								<Card>
									<CardHeader>
										<CardTitle>小时访问量</CardTitle>
									</CardHeader>
									<CardContent>
										<ReactECharts option={hourlyOption} style={{height: '300px'}}/>
									</CardContent>
								</Card>
							</div>
						</TabsContent>
					</Tabs>
				</Spinner>
			</DialogContent>
		</Dialog>
	)
}
