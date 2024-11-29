import { configureStore } from "@reduxjs/toolkit";
import LoginUser from "./LoginUser/index";
import Layout from "./Layout/index";
import Group from "@/stores/Group";

const store = configureStore({
    reducer:{
        LoginUser,
        Layout,
        Group
    }
})

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;