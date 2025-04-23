"use client"

import * as React from "react"
import { Eye, EyeOff } from 'lucide-react'
import { useForm } from "react-hook-form"
import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"

import { Button } from "@/components/ui/button"
import {
	Form,
	FormControl,
	FormField,
	FormItem,
	FormLabel,
	FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import {PasswordStrengthChecker} from "@/components/PasswordInput/components/password-strength-checker";

const passwordSchema = z
	.string()
	.min(8, "Password must be at least 8 characters")
	.regex(/[0-9]/, "Password must contain at least 1 number")
	.regex(/[a-z]/, "Password must contain at least 1 lowercase letter")
	.regex(/[A-Z]/, "Password must contain at least 1 uppercase letter")
	.regex(/[^a-zA-Z0-9]/, "Password must contain at least 1 special character")

const formSchema = z.object({
	password: passwordSchema,
})

interface PasswordInputProps {
	enableStrengthCheck?: boolean
}

export function NewPasswordInput({ enableStrengthCheck = true }: PasswordInputProps) {
	const [showPassword, setShowPassword] = React.useState(false)
	const [isFocused, setIsFocused] = React.useState(false)

	const form = useForm<z.infer<typeof formSchema>>({
		resolver: zodResolver(formSchema),
		mode: "onChange",
	})

	const password = form.watch("password") || ""

	const checkRequirements = (password: string) => {
		return [
			{
				text: "At least 8 characters",
				isMet: password.length >= 8,
			},
			{
				text: "At least 1 number",
				isMet: /[0-9]/.test(password),
			},
			{
				text: "At least 1 lowercase letter",
				isMet: /[a-z]/.test(password),
			},
			{
				text: "At least 1 uppercase letter",
				isMet: /[A-Z]/.test(password),
			},
			{
				text: "At least 1 special character",
				isMet: /[^a-zA-Z0-9]/.test(password),
			},
		]
	}

	const calculateStrength = (requirements: ReturnType<typeof checkRequirements>) => {
		const metRequirements = requirements.filter((req) => req.isMet).length
		return metRequirements / requirements.length
	}

	const requirements = checkRequirements(password)
	const strength = calculateStrength(requirements)

	return (
		<Form {...form}>
			<FormField
				control={form.control}
				name="password"
				render={({ field }) => (
					<FormItem className="space-y-1">
						<FormLabel>Password</FormLabel>
						<div className="relative">
							<FormControl>
								<Input
									type={showPassword ? "text" : "password"}
									{...field}
									onFocus={() => setIsFocused(true)}
									onBlur={() => setIsFocused(false)}
								/>
							</FormControl>
							<Button
								type="button"
								variant="ghost"
								size="icon"
								className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
								onClick={() => setShowPassword(!showPassword)}
							>
								{showPassword ? (
									<EyeOff className="h-4 w-4" />
								) : (
									<Eye className="h-4 w-4" />
								)}
								<span className="sr-only">
                  {showPassword ? "Hide password" : "Show password"}
                </span>
							</Button>
						</div>
						<FormMessage />
						{enableStrengthCheck && isFocused && (
							<PasswordStrengthChecker
								requirements={requirements}
								strength={strength}
							/>
						)}
					</FormItem>
				)}
			/>
		</Form>
	)
}

