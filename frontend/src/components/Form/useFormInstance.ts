"use client"
import { useContext } from "react";
import { FormInternalContext, AntdLikeFormInstance } from "./context";
import {FieldValues} from "react-hook-form";

export function useFormInstance<T extends FieldValues = never>() {
	const ctx = useContext(FormInternalContext);
	if (!ctx) throw new Error("useFormInstance must be used inside <Form>");
	return ctx.formInstance as AntdLikeFormInstance<T>;
}