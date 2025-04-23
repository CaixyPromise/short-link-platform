// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /auth/activate/${param0}/${param1} */
export async function doActivateUser(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.doActivateUserParams,
  body: API.UserActivationRequest,
  options?: { [key: string]: any },
) {
  const { token: param0, code: param1, ...queryParams } = params;
  return request<API.ResultBoolean>(`/auth/activate/${param0}/${param1}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: { ...queryParams },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /auth/get/login */
export async function getLoginUser(options?: { [key: string]: any }) {
  return request<API.ResultLoginUserVO>('/auth/get/login', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /auth/get/registration_info */
export async function getRegistrationInfoByParams(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getRegistrationInfoByParamsParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultRegistrationInfo>('/auth/get/registration_info', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/login */
export async function userLogin(body: API.UserLoginRequest, options?: { [key: string]: any }) {
  return request<API.ResultLoginUserVO>('/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /auth/login/wx_open */
export async function userLoginByWxOpen(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.userLoginByWxOpenParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLoginUserVO>('/auth/login/wx_open', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/logout */
export async function userLogout(options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/auth/logout', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /auth/oauth2/${param0}/callback */
export async function oAuthLoginCallback(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.oAuthLoginCallbackParams,
  options?: { [key: string]: any },
) {
  const { provider: param0, ...queryParams } = params;
  return request<any>(`/auth/oauth2/${param0}/callback`, {
    method: 'GET',
    params: {
      ...queryParams,
      allParams: undefined,
      ...queryParams['allParams'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /auth/oauth2/${param0}/login */
export async function initOAuthLogin(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.initOAuthLoginParams,
  options?: { [key: string]: any },
) {
  const { provider: param0, ...queryParams } = params;
  return request<API.ResultString>(`/auth/oauth2/${param0}/login`, {
    method: 'GET',
    params: {
      ...queryParams,
      authorizationUrlRequest: undefined,
      ...queryParams['authorizationUrlRequest'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/register */
export async function userRegister(
  body: API.UserRegisterRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
