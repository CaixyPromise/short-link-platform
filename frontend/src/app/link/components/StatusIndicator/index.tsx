"use client"

import * as React from "react"
import { motion, AnimatePresence } from "framer-motion"
import { Switch } from "@/components/ui/switch"
import { cn } from "@/lib/utils"
import {useEffect, useState} from "react";
import {useToast} from "@/hooks/use-toast";

interface StatusIndicatorProps {
    enabled: boolean
    onToggle?: (enabled: boolean) => Promise<{
        success: boolean
        newValue: boolean,
        message?: string
    }>
    loading?: boolean
}

export function StatusIndicator({ enabled, onToggle, loading = false }: StatusIndicatorProps) {
    const [internalEnabled, setInternalEnabled] = useState<boolean>(enabled)
    const [isHovered, setIsHovered] = useState<boolean>(false)
    const [isPending, setIsPending] = useState<boolean>(false)
    const {toast} = useToast()

    useEffect(() => {
        setInternalEnabled(enabled)
    }, [enabled])

    // 处理状态切换
    const handleToggle = async (checked: boolean) => {
        if (onToggle) {
            setIsPending(true)
            try {
                const {success, newValue, message} = await onToggle(checked)
                if (success) {
                    setInternalEnabled(newValue)
                }
                if (message) {
                    toast({ title: message })
                }
            } finally {
                setIsPending(false)
            }
        }
    }

    return (
        <motion.div
            className="relative inline-flex items-center gap-2"
            onHoverStart={() => setIsHovered(true)}
            onHoverEnd={() => setIsHovered(false)}
        >
            <AnimatePresence mode="wait">
                {isHovered ? (
                    <motion.div
                        key="switch"
                        initial={{ opacity: 0, scale: 0.9 }}
                        animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.9 }}
                        transition={{ duration: 0.15 }}
                    >
                        <Switch
                            checked={internalEnabled}
                            onCheckedChange={handleToggle}
                            disabled={isPending || loading}
                            className={cn(
                                "data-[state=checked]:bg-green-500",
                                "data-[state=unchecked]:bg-red-500",
                                isPending && "opacity-50 cursor-not-allowed"
                            )}
                        />
                    </motion.div>
                ) : (
                    <motion.div
                        key="status"
                        initial={{ opacity: 0, scale: 0.9 }}
                        animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.9 }}
                        transition={{ duration: 0.15 }}
                        className={cn(
                            "px-2 py-1 rounded-full text-sm font-medium",
                            internalEnabled ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700",
                            "relative overflow-hidden"
                        )}
                    >
                        {internalEnabled ? "已启用" : "未启用"}
                        <motion.div
                            className="absolute inset-0 bg-white"
                            animate={{
                                opacity: [0.1, 0.2, 0.1],
                            }}
                            transition={{
                                duration: 1.5,
                                repeat: Infinity,
                                ease: "easeInOut",
                            }}
                        />
                    </motion.div>
                )}
            </AnimatePresence>
        </motion.div>
    )
}

