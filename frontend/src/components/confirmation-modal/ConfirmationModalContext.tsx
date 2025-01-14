"use client"

import React, {createContext, useState, useContext, ReactNode} from 'react'
import ConfirmationModal from "@/components/confirmation-modal/index"

// 定义泛型接口，支持 ifValue 类型
interface ConfirmationModalProps<T extends never> {
	isOpen: boolean
	onClose: () => void
	onConfirm: (value?: T) => void
	onCancel?: (value?: T) => void
	title?: React.ReactNode
	description?: React.ReactNode
	icon?: React.ReactNode
	ifValue?: T
	confirmText?: React.ReactNode
	cancelText?: React.ReactNode
}

interface ConfirmationModalContextType {
	showConfirmation: <T extends never>(props: Omit<ConfirmationModalProps<T>, 'isOpen' | 'onClose'>) => void
	hideConfirmation: () => void
}

const ConfirmationModalContext = createContext<ConfirmationModalContextType | undefined>(undefined)

export const ConfirmationModalProvider: React.FC<{ children: ReactNode }> = ({children}) => {
	// 泛型状态，默认支持 any
	const [modalProps, setModalProps] = useState<ConfirmationModalProps<never> | null>(null)

	// showConfirmation 支持泛型，用于动态设置 ifValue 的类型
	const showConfirmation = <T extends never>(props: Omit<ConfirmationModalProps<T>, 'isOpen' | 'onClose'>) => {
		setModalProps({
			...props,
			isOpen: true,
			onClose: hideConfirmation,
		})
	}

	const hideConfirmation = () => {
		setModalProps(null)
	}

	return (
		<ConfirmationModalContext.Provider value={{showConfirmation, hideConfirmation}}>
			{children}
			{modalProps && (
				<ConfirmationModal
					{...modalProps}
				/>
			)}
		</ConfirmationModalContext.Provider>
	)
}

// 提供上下文 Hook
export const useConfirmationModal = () => {
	const context = useContext(ConfirmationModalContext)
	if (!context) {
		throw new Error('useConfirmationModal must be used within a ConfirmationModalProvider')
	}
	return context
}
