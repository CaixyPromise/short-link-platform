"use client"
import React, {useCallback, useEffect} from "react";
import {useAppDispatch} from "@/stores/hooks";
import {getLoginUser} from "@/api/authController";
import {setLoginUser} from "@/stores/LoginUser";
import {usePathname} from "next/navigation";
import {useToast} from "@/hooks/use-toast";

const InitLayoutProvider:React.FC<{
    children: React.ReactNode;
}> = ({children}) => {
    const dispatch = useAppDispatch();
    const pathname = usePathname();
    const {toast} = useToast();
    // 设置未登录逻辑
    const unLogin = useCallback(() => {
        toast({
            title: "获取用户信息失败",
            description: "请重新登录",
            duration: 3000,
        })
        // 切换到登录路径
        location.href = "/auth"
    },[toast]);

    const fetchUserInfo = useCallback(async () =>
    {
        // TODO: 获取用户信息
        try {
            const {data,code} = await getLoginUser();
            if (code === 0) {
                dispatch(setLoginUser(data))
            } else {
                unLogin()
            }
        }
        catch (e) {
            unLogin()
        }
    }, [])
    useEffect(() =>
    {
        if (pathname !== "/auth" ) {
            fetchUserInfo()
        }
    }, []);
    return children;
}

export default InitLayoutProvider;
