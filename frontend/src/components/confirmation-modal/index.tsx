import React from 'react'
import { AlertTriangle } from 'lucide-react'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"

interface ConfirmationModalProps<T> {
    isOpen: boolean
    onClose: () => void
    onConfirm: (value?: T) => void
    title?: React.ReactNode
    description?: React.ReactNode
    icon?: React.ReactNode;
    ifValue?: T
}

function ConfirmationModal<T>({
  isOpen,
  onClose,
  onConfirm,
  title = "确认操作",
  description = "是否继续该操作？",
  icon = <AlertTriangle className="h-6 w-6 text-yellow-500" />,
  ifValue
}: ConfirmationModalProps<T>) {
    return (
        <Dialog open={isOpen} onOpenChange={onClose} modal={false}>
            <DialogContent
                onInteractOutside={(event) => {
                    event.preventDefault();
                }}
            >
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        {icon}
                        {title}
                    </DialogTitle>
                    <DialogDescription>
                        {description}
                    </DialogDescription>
                </DialogHeader>
                <DialogFooter>
                    <Button variant="outline" onClick={onClose}>
                        取消
                    </Button>
                    <Button onClick={() => {
                        onConfirm?.(ifValue);
                        onClose?.();
                    }}>
                        确认
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}

export default ConfirmationModal;