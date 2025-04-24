'use client'

import {Button} from "@/components/ui/button"
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card"
import {AlertTriangle, RefreshCcw, Send} from "lucide-react"
import {useEffect} from "react"

export default function Error({error, reset,}: {
	error: Error & { digest?: string }
	reset: () => void
}) {
	useEffect(() => {
		console.error(error)
	}, [error])

	return (
		<div className="flex flex-col items-center justify-center min-h-screen bg-background text-foreground p-4">
			<Card className="w-full max-w-md">
				<CardHeader>
					<div className="w-12 h-12 rounded-full bg-destructive/10 flex items-center justify-center mx-auto mb-4">
						<AlertTriangle className="w-6 h-6 text-destructive"/>
					</div>
					<CardTitle className="text-2xl font-bold text-white text-center">Oops! Something went wrong</CardTitle>
					<CardDescription className="text-center">
						We apologize for the inconvenience. An unexpected error has occurred.
					</CardDescription>
				</CardHeader>
				<CardContent>
					<p className="text-sm text-muted-foreground text-center">
						Error: {error.message || "An unknown error occurred"}
					</p>
					{error.digest && (
						<p className="text-xs text-muted-foreground text-center mt-2">
							Error ID: {error.digest}
						</p>
					)}
				</CardContent>
				<CardFooter className="flex justify-center space-x-4">
					<Button onClick={reset} variant="outline">
						<RefreshCcw className="w-4 h-4 mr-2"/>
						Try again
					</Button>
					<Button onClick={() => window.location.href = '/'}>
						Back to Home
					</Button>
				</CardFooter>
			</Card>
			<Button variant="link" className="mt-8" onClick={() => {
				window.location.href = "/"
			}}>
				<Send className="w-4 h-4 mr-2"/>
				Report this issue
			</Button>
		</div>
	)
}