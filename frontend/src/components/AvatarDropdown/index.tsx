import React from 'react';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {Button} from "@/components/ui/button";
import {CircleUser, HandHelping, LogIn, LogOut, SmilePlus, UserCog} from "lucide-react";
import {useAppSelector} from "@/stores/hooks";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {Icon} from "@/components/ui/icons";
import {LoginUser} from "@/app/typing";
import {UserRoleEnum} from "@/enums/access";
import {queryServer} from "@/components/AvatarDropdown/server";
import {FormStateEnum} from "@/app/login/enums";

const AvatarDropdown: React.FC<{
    userInfo: LoginUser
}> = ({userInfo}) => {
    // 如果未登录
    if (userInfo.userRole === UserRoleEnum.NO_LOGIN) {
        return <>
            <DropdownMenuLabel className="whitespace-nowrap">
                <Icon.UserRole  className="sm:inline-block h-4 w-4 items-center mr-2" userRole={userInfo.userRole}/>
                欢迎，请登录
            </DropdownMenuLabel>
            <DropdownMenuSeparator/>
            <DropdownMenuItem onClick={() =>{
                window.location.href = `/login?action=${FormStateEnum.REGISTER.getCode()}`;
            }}><SmilePlus className="h-4 w-4 items-center mr-2" />去注册</DropdownMenuItem>
            <DropdownMenuItem onClick={() =>{
                window.location.href = `/login?action=${FormStateEnum.LOGIN.getCode()}`;
            }}><LogIn className="h-4 w-4 items-center mr-2" />去登录</DropdownMenuItem>
        </>
    }
    // 如果已经登录
    else {
        return <>
            <DropdownMenuLabel className="whitespace-nowrap">
                <Icon.UserRole  className="sm:inline-block h-4 w-4 items-center mr-2" userRole={userInfo.userRole}/>
                {userInfo.userName}
            </DropdownMenuLabel>
            <DropdownMenuSeparator/>
            <DropdownMenuItem onClick={() => {
                window.location.href = "/settings"
            }}><UserCog className="h-4 w-4 items-center mr-2" />设置</DropdownMenuItem>
            <DropdownMenuItem><HandHelping className="h-4 w-4 items-center mr-2"/>支持</DropdownMenuItem>
            <DropdownMenuSeparator/>
            <DropdownMenuItem onClick={async () => {
                const {code} = await queryServer.logOut();
                if (code === 0) {
                    window.location.href = "/login";
                }
            }}><LogOut className="h-4 w-4 items-center mr-2" />退出登录</DropdownMenuItem>
        </>
    }
}

const AvatarDropdownPage: React.FC = () =>
{
    const userInfo = useAppSelector(state => state.LoginUser);

    return (
        <>
            <DropdownMenu>
                <DropdownMenuTrigger asChild>
                    <Button variant="secondary" size="icon" className="rounded-full">
                        <Avatar className="h-8 w-8">
                            <AvatarImage src={userInfo.userAvatar} />
                            <AvatarFallback><CircleUser className="h-6 w-6" /></AvatarFallback>
                        </Avatar>
                        <span className="sr-only">Toggle user menu</span>
                    </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                    <AvatarDropdown userInfo={userInfo}/>
                </DropdownMenuContent>
            </DropdownMenu>
        </>
    )
}

export default AvatarDropdownPage;
