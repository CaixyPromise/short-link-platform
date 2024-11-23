import * as z from "zod"

export const formSchema = z.object({
    originUrl: z.string().url("域名格式不正确")
        .min(1, "原始链接不能为空"),
    createdType: z.number().int().min(0).max(1),
    linkName: z.string().min(1, "短链接名称不能为空").max(32, "短链接名称长度不能大于32"),
    validDateType: z.number().int().min(0).max(1),
    validDateRange: z.object({
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
        fromDateTime: z.date().nullable(), // 新增字段
        toDateTime: z.date().nullable(),   // 新增字段
    }).optional(),
    describe: z.string().max(1024, "描述长度不能超过1024").optional(),
})

export type FormValues = z.infer<typeof formSchema>

