import React, {
	forwardRef,
	useImperativeHandle,
} from "react";
import {
	FieldValues,
	Resolver,
	UseFormProps,
	FormProvider,
} from "react-hook-form";
import clsx from "clsx";
import {
	FormInternalContext,
	AntdLikeFormInstance,
} from "./context";
import {useAntdLikeForm, useAntdLikeFormWrapper} from "@/components/Form/useForm";
import {AntdLikeFormComponent} from "@/components/Form/types";
import {FormItem} from "@/components/Form/FormItem";
import {FormList} from "@/components/Form/FormList";

export interface FormProps<T extends FieldValues = FieldValues>
	extends Omit<UseFormProps<T>, "resolver"> {
	resolver?: Resolver<T>;
	layout?: "horizontal" | "vertical" | "inline";
	labelCol?: { span: number };
	wrapperCol?: { span: number };
	onFinish?: (values: T) => void;
	onFinishFailed?: (errors: any) => void;
	className?: string;
	style?: React.CSSProperties;
	children: React.ReactNode;
	form?: AntdLikeFormInstance<T>;
}

function FormInner<T extends FieldValues = FieldValues>(
	{
		resolver,
		form,
		onFinish,
		onFinishFailed,
		layout = "horizontal",
		labelCol,
		wrapperCol,
		className,
		style,
		children,
		...rest
	}: FormProps<T>,
	ref: React.Ref<AntdLikeFormInstance<T>>,
) {
	/* ① 如果父组件没传，就自己建一份 */
	const innerForm = useAntdLikeFormWrapper<T>({ resolver, ...rest });

	/* ② 只有一个最终实例 */
	const formInstance = form ?? innerForm;

	/* ③ 把提交包装一下（可选） */
	const handleSubmit = formInstance.rhf.handleSubmit(
		(vals) => onFinish?.(vals),
		(err) => onFinishFailed?.(err),
	);

	/* ④ 透出给 forwardRef */
	useImperativeHandle(ref, () => formInstance, [formInstance]);

	/* ⑤ className 之类纯 UI 逻辑 */
	const rootCls = clsx(
		"rhf-antd-form",
		"space-y-4",
		`rhf-form-${layout}`,
		className,
	);

	return (
		<FormInternalContext.Provider
			value={{
				formInstance,
				labelCol: labelCol?.span,
				wrapperCol: wrapperCol?.span,
			}}
		>
			<FormProvider {...formInstance.rhf}>
				<form
					className={rootCls}
					style={style}
					onSubmit={handleSubmit}
					autoComplete="off"
				>
					{children}
				</form>
			</FormProvider>
		</FormInternalContext.Provider>
	);
}

const Forward = forwardRef(FormInner) as unknown as AntdLikeFormComponent;

Forward.useForm = useAntdLikeForm;
Forward.Item = FormItem;
Forward.List = FormList;

export const Form = Forward;