import {LoginUser} from "@/app/typing";
import {useAppSelector} from "@/stores/hooks";


export function useUserInfo() : LoginUser {
    return useAppSelector(state => state.LoginUser);
}