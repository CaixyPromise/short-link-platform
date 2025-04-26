"use client"

import React from "react";
import { useController, FieldPath, FieldValues, RegisterOptions } from "react-hook-form";
import { Label } from "@/components/ui/label";
import { FormInternalContext } from "./context";
import clsx from "clsx";

export interface FormItemProps<T extends FieldValues = FieldValues> {
	/** Field name (dot‑notation supported) */
	name: FieldPath<T>;
	/** Shown beside control */
	label?: React.ReactNode;
	/** If provided, used instead of `value` for binding (e.g. `checked`) */
	valuePropName?: string;
	/** Validation & RHF rules */
	rules?: RegisterOptions;
	/** Mark required (adds *) */
	required?: boolean;
	/** Show description text below */
	description?: React.ReactNode;
	/** Remove all layout / label wrapper */
	noStyle?: boolean;
	/** Default value if not from Form.defaultValues */
	defaultValue?: Record<FieldPath<T>, never>;
	children: React.ReactElement | ((field: ReturnType<typeof useController>[0]) => React.ReactNode);
	className?: string;
	style?: React.CSSProperties;
	visible?: boolean;
}

export function FormItem<T extends FieldValues = FieldValues>(props: FormItemProps<T>) {
	const {
		name,
		label,
		valuePropName = "value",
		rules,
		required,
		description,
		noStyle,
		defaultValue,
		children,
		className,
		style,
		visible = true,
	} = props;
	if (!visible) return null;

	const ctx = React.useContext(FormInternalContext);
	if (!ctx) throw new Error("FormItem must be used within <Form>");

	const {
		field,
		fieldState,
	} = useController({
		name: name as FieldPath<T>,
		control: ctx.control,
		rules: { required, ...rules },
		defaultValue,
	});

	// Build child element or render props
	const injected = React.useMemo(() => {
		if (typeof children === "function") return children(field);
		if (!React.isValidElement(children)) return children;
		// onChange name detection (onChange / onSelect / ...)
		const handlerName = (children.props.onChange ? "onChange" : undefined) || "onChange";
		return React.cloneElement(children, {
			[valuePropName]: field.value,
			[handlerName]: (...args: any[]) => {
				// RHF update
				field.onChange(...args);
				// call original
				children.props[handlerName]?.(...args);
			},
			ref: (instance: any) => {
				if (instance && typeof instance.focus === 'function') {
					field.ref(instance);
				}
			},
		});
	}, [children, field, valuePropName]);

	// ----- Render -----
	if (noStyle) return injected;

	const err = fieldState.error?.message;
	const rootCls = clsx("rhf-form-item", className);
	const labelCls = clsx("rhf-form-item-label", { required });

	return (
		<div className={rootCls} style={style}>
			{label != null && (
				<Label className={labelCls}>{label}</Label>
			)}
			<div className="rhf-form-item-control">
				{injected}
				{description && <div className="rhf-form-item-desc text-[0.8rem] text-muted-foreground mt-1">{description}</div>}
				{err && <div role="alert" className="rhf-form-item-error text-red-600">{err}</div>}
			</div>
		</div>
	);
}