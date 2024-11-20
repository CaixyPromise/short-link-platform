// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /admin/forceLogout */
export async function forceLogout(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.forceLogoutParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/admin/forceLogout', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /admin/onlineUsers */
export async function getOnlineUsers(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getOnlineUsersParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageUserVO>('/admin/onlineUsers', {
    method: 'GET',
    params: {
      // page has a default value: 1
      page: '1',
      // size has a default value: 10
      size: '10',
      ...params,
    },
    ...(options || {}),
  });
}
