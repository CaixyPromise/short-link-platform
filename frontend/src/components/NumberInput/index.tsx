"use client"

import * as React from "react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { ChevronUp, ChevronDown } from 'lucide-react'

interface NumberInputProps {
    value: number
    onChange: (value: number) => void
    min?: number
    max?: number
    step?: number
}

export default function NumberInput({ value, onChange, min = 0, max = 100, step = 1 }: NumberInputProps) {
    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = parseFloat(e.target.value)
        if (!isNaN(newValue) && newValue >= min && newValue <= max) {
            onChange(newValue)
        }
    }

    const increment = () => {
        const newValue = Math.min(value + step, max)
        onChange(newValue)
    }

    const decrement = () => {
        const newValue = Math.max(value - step, min)
        onChange(newValue)
    }

    return (
        <div className="relative w-fit">
            <Input
                value={value}
                onChange={handleInputChange}
                min={min}
                max={max}
                step={step}
                className="pr-8"
            />
            <div className="absolute right-1 top-1 flex flex-col space-y-0.1">
                <Button
                    type="button"
                    variant="ghost"
                    size="icon"
                    className="h-4 w-4 p-0"
                    onClick={increment}
                >
                    <ChevronUp className="h-2 w-2" />
                </Button>
                <Button
                    type="button"
                    variant="ghost"
                    size="icon"
                    className="h-4 w-4 p-0"
                    onClick={decrement}
                >
                    <ChevronDown className="h-2 w-2" />
                </Button>
            </div>
        </div>
    )
}