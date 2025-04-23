import {Check, X} from 'lucide-react'
import {cn} from "@/lib/utils"

interface Requirement {
	text: string
	isMet: boolean
}

interface PasswordStrengthCheckerProps {
	requirements: Requirement[]
	strength: number
}

export function PasswordStrengthChecker({
  requirements,
  strength,
}: PasswordStrengthCheckerProps) {
	const getStrengthText = (strength: number) => {
		if (strength === 0) return "Very weak password."
		if (strength < 0.5) return "Weak password."
		if (strength < 0.8) return "Medium strength password."
		return "Strong password."
	}

	return (
		<div className="mt-2 p-4 border rounded-md bg-background shadow-sm space-y-3">
			<div className="space-y-2">
				<div className="h-2 w-full bg-secondary rounded-full">
					<div
						className={cn(
							"h-full rounded-full transition-all",
							strength < 0.3 ? "bg-destructive" : strength < 0.7 ? "bg-orange-500" : "bg-green-500"
						)}
						style={{width: `${strength * 100}%`}}
					/>
				</div>
				<p className="text-sm text-muted-foreground">
					{getStrengthText(strength)}
				</p>
			</div>
			<div className="space-y-2">
				{requirements.map((requirement, index) => (
					<div key={index} className="flex items-center gap-2">
						{requirement.isMet ? (
							<Check className="h-4 w-4 text-green-500"/>
						) : (
							<X className="h-4 w-4 text-destructive"/>
						)}
						<span className="text-sm text-muted-foreground">
              {requirement.text}
            </span>
					</div>
				))}
			</div>
		</div>
	)
}

