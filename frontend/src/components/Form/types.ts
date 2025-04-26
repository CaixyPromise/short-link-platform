import { FieldValues } from "react-hook-form";
import React from "react";
import { AntdLikeFormInstance } from "./context";
import { FormProps } from "./Form";
import { FormItem } from "./FormItem";
import { FormList } from "./FormList";
import {useAntdLikeForm} from "@/components/Form/useForm";

/**
 * 组件主体是一个带泛型的 FunctionComponent：
 *
 *  (props) => React.ReactNode
 *
 * 然而我们还要把静态字段挂到它身上。这里声明一个交叉类型即可。
 */
export type AntdLikeFormComponent = {
	/** 主组件函数本体（支持 ref & 泛型） */
		<T extends FieldValues = FieldValues>(
		props: FormProps<T> & { ref?: React.Ref<AntdLikeFormInstance<T>> }
	): React.ReactNode;
	/** Ant-style 工厂：Form.useForm */
	useForm: typeof useAntdLikeForm;
	/** 别名挂载 */
	Item: typeof FormItem;
	List: typeof FormList;
};
