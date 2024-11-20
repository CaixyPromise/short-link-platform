import {MenuItemProps} from "@/app/typing";
import {UserRoleEnum} from "@/enums/access";

export const NavItem: MenuItemProps[] = [
    {name: "登录/注册页", path:"/login", layout: false, hiddenInMenu: true},
    {name: "Welcome", path: "/", icon: "dashboard"},
    // {name: "dashboard", path: "/dashboard", icon: "dashboard"},
    {name: "管理后台", path: "/admin", icon: "user", access: UserRoleEnum.ADMIN,
        children: [
            {name: "用户管理", path: "/admin/user", access: UserRoleEnum.ADMIN},
            {name: "在线用户", path: "/admin/online", access: UserRoleEnum.ADMIN},
        ]
    },
    {name: "Settings", path: "/settings", icon: "setting", access: UserRoleEnum.IS_LOGIN},
]


