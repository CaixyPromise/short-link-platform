import { configureStore } from "@reduxjs/toolkit";
import LoginUser from "./LoginUser/index";
import Layout from "./Layout/index";

const store = configureStore({
    reducer:{
        LoginUser,
        Layout
    }
})

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;