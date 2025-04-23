import axios from "axios";
import {BASE_URL, LOGIN_TOKEN_KEY, LOGIN_TYPE, LOGIN_TYPE_TOKEN} from "@/constant/env";
import {LocalStorageUtil} from "@/lib/LocalStorageUtil";

// 创建 Axios 示例
const axiosInstance = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
    withCredentials: true,
});

// 创建请求拦截器
axiosInstance.interceptors.request.use(
    function (config) {
        if (LOGIN_TYPE === LOGIN_TYPE_TOKEN) {
            config.headers = {
                ...config.headers,
                Authorization: `Bearer ${LocalStorageUtil.getItem(LOGIN_TOKEN_KEY)}`,
            };
        }
        // 请求执行前执行
        return config;
    },
    function (error) {
        // 处理请求错误
        return Promise.reject(error);
    },
);

// 创建响应拦截器
axiosInstance.interceptors.response.use(
    // 2xx 响应触发
    function (response) {
        // 处理响应数据
        const { data } = response;
        // 未登录
        if (data.code === 40100) {
            console.log("未登录, data: ", data, response.request.responseURL)
            // 不是获取用户信息接口，或者不是登录页面，则跳转到登录页面
            if (
                !response.request.responseURL.includes("user/get/login") &&
                !window.location.pathname.includes("/auth")
            ) {
                window.location.href = `/auth?redirect=${window.location.href}`;
            }
        } else if (data.code !== 0) {
            // 其他错误
            throw new Error(data.message ?? "服务器错误");
        }
        return data;
    },
    // 非 2xx 响应触发
    function (error) {
        // 处理响应错误
        return Promise.reject(error);
    },
);

export default axiosInstance;

