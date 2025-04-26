"use client"
import { createContext } from "react";
import type {
	Control,  FieldValues, UseFormReturn,
} from "react-hook-form";

export interface AntdLikeFormInstance<T extends FieldValues = FieldValues> {
	getFieldValue: <K extends keyof T>(name: K) => T[K];
	getFieldsValue: () => T;
	setFieldValue: <K extends keyof T>(name: K, value: T[K]) => void;
	setFieldsValue: (values: Partial<T>) => void;
	resetFields: (values?: Partial<T>) => void;
	validateFields: () => Promise<T>;
	submit: () => void;
	rhf: UseFormReturn<T>;
}

export type InternalFormContext<T extends FieldValues = FieldValues> = {
	formInstance: AntdLikeFormInstance<T>;
	control: Control<T>;
	labelCol?: number;
	wrapperCol?: number;
};

export const FormInternalContext = createContext<InternalFormContext<never> | null>(null);