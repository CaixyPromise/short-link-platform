import {FieldValues, useForm, UseFormProps, UseFormReturn} from "react-hook-form";
import React from "react";

export interface AntdLikeFormInstance<T extends FieldValues = never> extends UseFormReturn<T> {
	getFieldValue: <K extends keyof T>(name: K) => T[K];
	setFieldValue: <K extends keyof T>(name: K, value: T[K]) => void;
	resetFields: (names?: (keyof T)[]) => void;
	validateFields: (names?: (keyof T)[]) => Promise<boolean>;
	submit: () => void;
	rhf: UseFormReturn<T>;
}

/** 把 R H F 的 methods → 注入 Ant-style API */
function buildAntdLikeAPI<T extends FieldValues>(m: UseFormReturn<T>): AntdLikeFormInstance<T> {
	return {
		...m,
		getFieldValue: (name) => m.getValues(name as never),
		setFieldValue: (name, value) =>
			m.setValue(name as never, value, {shouldDirty: true}),
		resetFields: (names) =>
			names ? m.reset({...m.getValues(), ...Object.fromEntries(names.map((n) => [n, undefined]))}) : m.reset(),
		validateFields: (names) => m.trigger(names as never),
		submit: () => m.handleSubmit(() => {/**/}),
		rhf: m
	}
}


/** 真正的 Hook——在组件顶层调用 */
export function useAntdLikeFormWrapper<T extends FieldValues = never>(
	opts?: UseFormProps<T>
): AntdLikeFormInstance<T> {
	const methods = useForm<T>(opts);
	return React.useMemo(() => buildAntdLikeAPI(methods), [methods]);
}

export function useAntdLikeForm<T extends FieldValues = never>(
	opts?: UseFormProps<T>,
): [AntdLikeFormInstance<T>] {
	const instance = useAntdLikeFormWrapper<T>(opts);
	return React.useMemo(() => [instance] as [AntdLikeFormInstance<T>], [instance]);
}
