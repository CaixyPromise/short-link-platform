"use client"
import React from "react";
import { useFieldArray, FieldValues, FieldArrayWithId } from "react-hook-form";
import { FormInternalContext } from "./context";

interface RenderFnArgs<T extends FieldValues> {
	fields: FieldArrayWithId<T, never, never>[];
	operations: {
		add: () => void;
		remove: (index: number) => void;
		move: (from: number, to: number) => void;
	};
}

export interface FormListProps<T extends FieldValues = FieldValues> {
	name: string; // root list name
	children: (args: RenderFnArgs<T>) => React.ReactNode;
}

export function FormList<T extends FieldValues = FieldValues>({ name, children }: FormListProps<T>) {
	const ctx = React.useContext(FormInternalContext);
	if (!ctx) throw new Error("Form.List must be used within <Form>");

	const { control } = ctx;
	const { fields, append, remove, move } = useFieldArray({ control, name });

	return (
		<div className="rhf-form-list">
			{children({
				fields,
				operations: {
					add: () => append({}),
					remove,
					move,
				},
			})}
		</div>
	);
}