import { Button } from "@/components/ui/button"
import { HelpCircle } from "lucide-react"
import Link from "next/link"

export default function NotFound()
{
    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-background text-foreground p-4">
            <div className="text-center space-y-6 max-w-md">
                <div className="relative w-32 h-32 mx-auto">
                    <div className="absolute inset-0 bg-blue-100 rounded-full"></div>
                    <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
                        <HelpCircle className="w-16 h-16 text-blue-500" />
                    </div>
                </div>
                <h1 className="text-6xl font-bold">404</h1>
                <p className="text-xl text-muted-foreground">Sorry, the page you visited does not exist.</p>
                <Button asChild className="mt-4">
                    <Link href="/">Back Home</Link>
                </Button>
            </div>
        </div>
    )
}