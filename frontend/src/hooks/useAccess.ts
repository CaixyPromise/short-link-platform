import {useAppSelector} from "@/stores/hooks";
import {canAccess} from "@/access/checkAccess";
import {UserRoleEnum} from "@/enums/access";
import {useMemo} from "react";

const useAccess = (access: UserRoleEnum | undefined) => {
    const { userRole } = useAppSelector(state => state.LoginUser);

    return useMemo(() => ({
        userRole,
        canAccess: canAccess(userRole, access),
        canAdmin: canAccess(userRole, UserRoleEnum.ADMIN)
    }), [userRole, access]);
};


export default useAccess;