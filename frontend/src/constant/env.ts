
export const API_DEV = 'http://localhost:9998/api';

export const API_PRODUCTION = 'https://api.caixyowo.cn';

export const BASE_URL = process.env.NODE_ENV === 'development' ? API_DEV : API_PRODUCTION;

export const LOGIN_TYPE_TOKEN = "TOKEN";
export const LOGIN_TYPE_SESSION = "SESSION";

export const LOGIN_TYPE = LOGIN_TYPE_TOKEN;

export const LOGIN_TOKEN_KEY = "TOKEN";
