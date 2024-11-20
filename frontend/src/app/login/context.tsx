import {Context, createContext, Dispatch} from "react";
import {FormStateEnum} from "@/app/login/enums";


const FormStateContext: Context<{
    formState: FormStateEnum,
    setFormState: Dispatch<FormStateEnum>
    userAccount: string,
    setUserAccount: (userAccount: string) => void,
}> = createContext({
    formState: FormStateEnum.LOGIN,
    setFormState: (formState: FormStateEnum) => {},
    userAccount: "",
    setUserAccount: (userAccount: string) => {}
})

export default FormStateContext;