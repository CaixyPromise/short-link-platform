"use client"

import { useToast } from "@/hooks/use-toast"
import {
  Toast,
  ToastClose,
  ToastDescription,
  ToastProvider,
  ToastTitle,
  ToastViewport,
} from "@/components/ui/toast"

export function Toaster() {
  const { toasts } = useToast()
  return (
      <ToastProvider>
        {toasts.map(function ({ id, title, description, action, ...props }) {
          return (
              <Toast key={id} {...props}>
                <div className="grid gap-1">
                  {title && <ToastTitle>{title}</ToastTitle>}
                  {typeof description === 'string' ? (
                      <ToastDescription>{description}</ToastDescription>
                  ) : (
                      <div>{description}</div>
                  )}
                </div>
                {action}
                <ToastClose />
              </Toast>
          )
        })}
        {toasts.length > 0 && <ToastViewport position={toasts[0]?.position || "bottom-right"} />}
      </ToastProvider>
  )
}