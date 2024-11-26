// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /link/add */
export async function addLink(body: API.LinkAddRequest, options?: { [key: string]: any }) {
  return request<API.ResultLinkCreateVO>('/link/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /link/delete */
export async function deleteLink(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/link/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /link/edit */
export async function editLink(body: API.LinkEditRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/link/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /link/get/vo */
export async function getLinkVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkVO>('/link/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /link/list/page/vo */
export async function listLinkVoByPage(
  body: API.LinkQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkVO>('/link/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /link/update */
export async function updateLink(body: API.LinkUpdateRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/link/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
