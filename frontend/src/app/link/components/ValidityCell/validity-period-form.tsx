"use client"

import * as React from "react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import * as z from "zod"
import { Button } from "@/components/ui/button"
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { ValidDateEnum } from "@/enums/ValidDateEnum"
import DateTimeRangePicker from "@/components/DateTimeRangePicker"

const formSchema = z.object({
    validDateType: z.number(),
    validDateRange: z
        .object({
            from: z.date().nullable(),
            to: z.date().nullable(),
            fromTime: z.object({
                hours: z.string(),
                minutes: z.string(),
                seconds: z.string(),
            }),
            toTime: z.object({
                hours: z.string(),
                minutes: z.string(),
                seconds: z.string(),
            }),
            fromDateTime: z.date().nullable(),
            toDateTime: z.date().nullable(),
        })
        .optional()
        .refine(
            (data) => !!data?.from && !!data?.to,
            {
                message: "必须选择有效的时间范围",
                path: ["from"],
            }
        )
        .optional(),
})

interface ValidityPeriodFormProps {
    defaultValues: {
        validDateType: number
        validDateStart?: string
        validDateEnd?: string
    }
    onSubmit: (values: z.infer<typeof formSchema>) => Promise<void>
}

export function ValidityPeriodForm({ defaultValues, onSubmit }: ValidityPeriodFormProps) {
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            validDateType: defaultValues.validDateType,
            validDateRange: defaultValues.validDateStart && defaultValues.validDateEnd
                ? {
                    from: new Date(defaultValues.validDateStart),
                    to: new Date(defaultValues.validDateEnd),
                    fromTime: {
                        hours: new Date(defaultValues.validDateStart).getHours().toString().padStart(2, "0"),
                        minutes: new Date(defaultValues.validDateStart).getMinutes().toString().padStart(2, "0"),
                        seconds: new Date(defaultValues.validDateStart).getSeconds().toString().padStart(2, "0"),
                    },
                    toTime: {
                        hours: new Date(defaultValues.validDateEnd).getHours().toString().padStart(2, "0"),
                        minutes: new Date(defaultValues.validDateEnd).getMinutes().toString().padStart(2, "0"),
                        seconds: new Date(defaultValues.validDateEnd).getSeconds().toString().padStart(2, "0"),
                    },
                    fromDateTime: new Date(defaultValues.validDateStart),
                    toDateTime: new Date(defaultValues.validDateEnd),
                }
                : undefined,
        },
    })

    const validDateType = form.watch("validDateType")
    const isDirty = form.formState.isDirty

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                <FormField
                    control={form.control}
                    name="validDateType"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>有效期类型</FormLabel>
                            <Select
                                onValueChange={(value) => {
                                    const selectedType = parseInt(value)
                                    field.onChange(selectedType)
                                    if (selectedType === ValidDateEnum.PERMANENT) {
                                        form.setValue("validDateRange", undefined)
                                    }
                                }}
                                defaultValue={field.value.toString()}
                            >
                                <FormControl>
                                    <SelectTrigger>
                                        <SelectValue placeholder="选择有效期类型" />
                                    </SelectTrigger>
                                </FormControl>
                                <SelectContent>
                                    <SelectItem value={ValidDateEnum.PERMANENT.toString()}>永久有效</SelectItem>
                                    <SelectItem value={ValidDateEnum.SPECIFIED.toString()}>指定时间</SelectItem>
                                </SelectContent>
                            </Select>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                {validDateType === ValidDateEnum.SPECIFIED && (
                    <FormField
                        control={form.control}
                        name="validDateRange"
                        render={() => (
                            <FormItem>
                                <FormLabel>有效期范围</FormLabel>
                                <FormControl>
                                    <Controller
                                        control={form.control}
                                        name="validDateRange"
                                        render={({ field }) => (
                                            <DateTimeRangePicker
                                                value={field.value}
                                                onChange={field.onChange}
                                                disablePast
                                            />
                                        )}
                                    />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                )}

                {isDirty && (
                    <div className="flex justify-end gap-4">
                        <Button type="submit">保存</Button>
                    </div>
                )}
            </form>
        </Form>
    )
}
