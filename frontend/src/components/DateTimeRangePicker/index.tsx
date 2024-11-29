"use client"

import { format, isBefore, startOfToday } from "date-fns"
import zhCN from "date-fns/locale/zh-CN"
import { CalendarIcon } from 'lucide-react'
import { DateRange } from "react-day-picker"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "@/components/ui/popover"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import React from "react";
import {DateUtils} from "@/lib/DateUtils";

interface TimeSelection {
    hours: string
    minutes: string
    seconds: string
}

interface DateTimeRange {
    from?: Date
    to?: Date
    fromTime?: TimeSelection
    toTime?: TimeSelection
    fromDateTime?: Date
    toDateTime?: Date
}



interface DateTimeRangePickerProps extends React.HTMLAttributes<HTMLDivElement> {
    value: DateTimeRange | undefined
    onChange: (value: DateTimeRange | undefined) => void
    disablePast?: boolean
}
export default function DateTimeRangePicker({
                                                className,
                                                value,
                                                onChange,
                                                disablePast = false,
                                            }: DateTimeRangePickerProps) {
    const dateTime: DateTimeRange = {
        from: value?.from,
        to: value?.to,
        fromTime: value?.fromTime || { hours: "00", minutes: "00", seconds: "00" },
        toTime: value?.toTime || { hours: "00", minutes: "00", seconds: "00" },
        fromDateTime: value?.fromDateTime,
        toDateTime: value?.toDateTime,
    }



    const updateDateTime = (newDateTime: DateTimeRange) => {
        const combinedDateTime = { ...newDateTime }

        // 组合 fromDateTime
        if (newDateTime.from && newDateTime.fromTime) {
            combinedDateTime.fromDateTime = DateUtils.mergeDateAndTime_V2(newDateTime.from, newDateTime.fromTime)
        } else {
            combinedDateTime.fromDateTime = undefined
        }

        // 组合 toDateTime
        if (newDateTime.to && newDateTime.toTime) {
            combinedDateTime.toDateTime = DateUtils.mergeDateAndTime_V2(newDateTime.to, newDateTime.toTime)
        } else {
            combinedDateTime.toDateTime = undefined
        }

        onChange(combinedDateTime)
    }


    const updateTime = (type: 'fromTime' | 'toTime', field: keyof TimeSelection, val: string) => {
        const updatedTime = {
            ...dateTime[type],
            [field]: val,
        }
        const newDateTime = {
            ...dateTime,
            [type]: updatedTime,
        }
        updateDateTime(newDateTime)
    }

    const onDateSelect = (range: DateRange | undefined) => {
        const newDateTime = {
            ...dateTime,
            from: range?.from,
            to: range?.to,
        }
        updateDateTime(newDateTime)
    }


    const TimeSelector = ({ type, className }: { type: 'fromTime' | 'toTime', className?: string }) => {
        const timeValue = dateTime[type] || { hours: "00", minutes: "00", seconds: "00" }

        // 动态生成可选项，根据开始和结束日期的关系调整时间范围
        const generateOptions = (max: number, start: number = 0) =>
            Array.from({ length: max - start }, (_, i) => start + i).map((i) => i.toString().padStart(2, '0'))

        // 获取小时、分钟、秒的可选项
        let hourOptions = generateOptions(24)
        let minuteOptions = generateOptions(60)
        let secondOptions = generateOptions(60)

        const now = new Date()

        if (disablePast) {
            if (type === 'fromTime' && dateTime.from) {
                const selectedDate = new Date(dateTime.from)
                if (selectedDate.toDateString() === now.toDateString()) {
                    // 如果开始日期是今天，禁用当前时间之前的时间
                    const currentHour = now.getHours()
                    const currentMinute = now.getMinutes()
                    const currentSecond = now.getSeconds()

                    hourOptions = generateOptions(24, currentHour)
                    if (parseInt(timeValue.hours, 10) === currentHour) {
                        minuteOptions = generateOptions(60, currentMinute)
                        if (parseInt(timeValue.minutes, 10) === currentMinute) {
                            secondOptions = generateOptions(60, currentSecond + 1)
                        }
                    }
                }
            }

            if (type === 'toTime' && dateTime.from && dateTime.to) {
                const fromDate = new Date(dateTime.from)
                const toDate = new Date(dateTime.to)
                if (fromDate.toDateString() === toDate.toDateString()) {
                    // 当开始日期和结束日期相同时，调整结束时间的可选项
                    const fromHours = parseInt(dateTime.fromTime?.hours || '0', 10)
                    const fromMinutes = parseInt(dateTime.fromTime?.minutes || '0', 10)
                    const fromSeconds = parseInt(dateTime.fromTime?.seconds || '0', 10)

                    hourOptions = generateOptions(24, fromHours)
                    if (parseInt(timeValue.hours, 10) === fromHours) {
                        minuteOptions = generateOptions(60, fromMinutes)
                        if (parseInt(timeValue.minutes, 10) === fromMinutes) {
                            secondOptions = generateOptions(60, fromSeconds + 1)
                        }
                    }
                } else if (toDate.toDateString() === now.toDateString()) {
                    // 如果结束日期是今天，禁用当前时间之前的时间
                    const currentHour = now.getHours()
                    const currentMinute = now.getMinutes()
                    const currentSecond = now.getSeconds()

                    hourOptions = generateOptions(24, currentHour)
                    if (parseInt(timeValue.hours, 10) === currentHour) {
                        minuteOptions = generateOptions(60, currentMinute)
                        if (parseInt(timeValue.minutes, 10) === currentMinute) {
                            secondOptions = generateOptions(60, currentSecond + 1)
                        }
                    }
                }
            }
        }

        if (type === 'toTime' && dateTime.from && dateTime.to) {
            const fromDate = new Date(dateTime.from)
            const toDate = new Date(dateTime.to)
            if (fromDate.toDateString() === toDate.toDateString()) {
                // 当开始日期和结束日期相同时，调整结束时间的可选项
                const fromHours = parseInt(dateTime.fromTime?.hours || '0', 10)
                const fromMinutes = parseInt(dateTime.fromTime?.minutes || '0', 10)
                const fromSeconds = parseInt(dateTime.fromTime?.seconds || '0', 10)

                hourOptions = generateOptions(24, fromHours)
                if (parseInt(timeValue.hours, 10) === fromHours) {
                    minuteOptions = generateOptions(60, fromMinutes)
                    if (parseInt(timeValue.minutes, 10) === fromMinutes) {
                        secondOptions = generateOptions(60, fromSeconds + 1)
                    }
                }
            }
        }

        return (
            <div className={cn("flex gap-2", className)}>
                <Select
                    value={timeValue.hours}
                    onValueChange={(value) => updateTime(type, 'hours', value)}
                >
                    <SelectTrigger className="w-[70px]">
                        <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                        {hourOptions.map((value) => (
                            <SelectItem key={value} value={value}>
                                {value}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
                <Select
                    value={timeValue.minutes}
                    onValueChange={(value) => updateTime(type, 'minutes', value)}
                >
                    <SelectTrigger className="w-[70px]">
                        <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                        {minuteOptions.map((value) => (
                            <SelectItem key={value} value={value}>
                                {value}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
                <Select
                    value={timeValue.seconds}
                    onValueChange={(value) => updateTime(type, 'seconds', value)}
                >
                    <SelectTrigger className="w-[70px]">
                        <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                        {secondOptions.map((value) => (
                            <SelectItem key={value} value={value}>
                                {value}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
            </div>
        )
    }

    return (
        <div className={cn("grid gap-2", className)}>
            <Popover>
                <PopoverTrigger asChild>
                    <Button
                        id="date"
                        variant="outline"
                        className={cn(
                            "w-full justify-start text-left font-normal truncate overflow-hidden",
                            !dateTime && "text-muted-foreground"
                        )}
                    >
                        <CalendarIcon className="mr-2 h-4 w-4 shrink-0" />
                        {dateTime?.from ? (
                            dateTime.to ? (
                                <>
                                    {format(dateTime.from, "yyyy-MM-dd", { locale: zhCN })} {dateTime.fromTime?.hours}:{dateTime.fromTime?.minutes}:{dateTime.fromTime?.seconds} -{" "}
                                    {format(dateTime.to, "yyyy-MM-dd", { locale: zhCN })} {dateTime.toTime?.hours}:{dateTime.toTime?.minutes}:{dateTime.toTime?.seconds}
                                </>
                            ) : (
                                format(dateTime.from, "yyyy-MM-dd", { locale: zhCN })
                            )
                        ) : (
                            <span>请选择日期</span>
                        )}
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="start">
                    <div className="flex flex-col">
                        <Calendar
                            initialFocus
                            mode="range"
                            defaultMonth={dateTime?.from}
                            selected={dateTime.from || dateTime.to ? { from: dateTime.from, to: dateTime.to } : undefined}
                            onSelect={onDateSelect}
                            numberOfMonths={2}
                            className="border-b"
                            locale={zhCN}
                            disabled={disablePast ? (date) => isBefore(date, startOfToday()) : undefined}
                        />

                        <div className="flex justify-between p-3 bg-muted/5">
                            <TimeSelector type="fromTime" />
                            <TimeSelector type="toTime" />
                        </div>
                    </div>
                </PopoverContent>
            </Popover>
        </div>
    )
}
