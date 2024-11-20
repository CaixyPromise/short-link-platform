import {UserRoleEnum} from "@/enums/access";

export const canAccess = (userRole: UserRoleEnum, needAccess: UserRoleEnum = UserRoleEnum.NO_LOGIN) => {
    switch (needAccess)
    {
        case UserRoleEnum.NO_LOGIN:
            return true;
        case UserRoleEnum.USER:
            return userRole === UserRoleEnum.USER || userRole === UserRoleEnum.ADMIN;
        case UserRoleEnum.ADMIN:
            return userRole === UserRoleEnum.ADMIN;
        case UserRoleEnum.IS_LOGIN:
            return userRole !== UserRoleEnum.NO_LOGIN && userRole !== UserRoleEnum.BAN;
        default:
            return false; // 默认情况为 false，包括BAN
    }
};
