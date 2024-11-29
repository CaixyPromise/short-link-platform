// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 GET /${param0} */
export async function redirectUrl(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.redirectUrlParams,
  options?: { [key: string]: any },
) {
  const { shortUri: param0, ...queryParams } = params;
  return request<any>(`/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}
