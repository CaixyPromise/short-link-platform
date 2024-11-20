import {UserRoleEnum} from "@/enums/access";
import {LoginUser} from "@/app/typing";

export const DEFAULT_USER = {
    userName: "用户未登录",
    userAvatar: "",
    id: "",
    userRole: UserRoleEnum.NO_LOGIN,
    userEmail: "",
    userGender: 0
} as LoginUser