import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {DEFAULT_USER} from "./constant";
import {LoginUser} from "@/app/typing";
import {LOGIN_TOKEN_KEY, LOGIN_TYPE, LOGIN_TYPE_TOKEN} from "@/constant/env";
import {LocalStorageUtil} from "@/lib/LocalStorageUtil";

/**
 * 登录用户全局状态
 */
export const loginUserSlice = createSlice({
    name: "loginUser",
    initialState: DEFAULT_USER,
    reducers: {
        setLoginUser: (state, action: PayloadAction<LoginUser>) =>
        {
            if (action.payload !== undefined)
            {
                if (LOGIN_TYPE === LOGIN_TYPE_TOKEN) {
                    const {token} = action.payload;
                    if (token) {
                        LocalStorageUtil.setItem(LOGIN_TOKEN_KEY, token);
                    }
                }
                return {
                    ...action.payload,
                };
            }
        },
    },
});

// 修改状态
export const {setLoginUser} = loginUserSlice.actions;

export default loginUserSlice.reducer;
