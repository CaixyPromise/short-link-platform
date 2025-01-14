"use client"
import React, { createContext, useContext, useState, ReactNode } from 'react';
import ResultWithDialog from "@/components/Result/components/ResultWithDialog";

interface DialogContextType {
	isOpen: boolean;
	openDialog: (params: ResultPageProps) => void;
	closeDialog: () => void;
	resultParams: ResultPageProps | null;
}

const DialogContext = createContext<DialogContextType | undefined>(undefined);

export interface ResultPageProps {
	status: 'success' | 'failure';
	title: string;
	subText: string;
	extraContent?: ReactNode;
	modal?: boolean
	children?: ReactNode;
}

export const DialogProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
	const [isOpen, setIsOpen] = useState(false);
	const [resultParams, setResultParams] = useState<ResultPageProps | null>(null);

	const openDialog = (params: ResultPageProps) => {
		setResultParams(params);
		setIsOpen(true);
	};

	const closeDialog = () => {
		setIsOpen(false);
		setResultParams(null);
	};

	return (
		<DialogContext.Provider value={{ isOpen, openDialog, closeDialog, resultParams }}>
			{children}
			<ResultWithDialog
				isOpen={isOpen}
				closeDialog={closeDialog}
				resultParams={resultParams}
			/>
		</DialogContext.Provider>
	);
};

export const useDialog = () => {
	const context = useContext(DialogContext);
	if (context === undefined) {
		throw new Error('useDialog must be used within a DialogProvider');
	}
	return context;
};

