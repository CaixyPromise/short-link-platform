// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /linkGoto/add */
export async function addLinkGoto(body: API.LinkGotoAddRequest, options?: { [key: string]: any }) {
  return request<API.ResultLong>('/linkGoto/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkGoto/delete */
export async function deleteLinkGoto(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/linkGoto/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkGoto/edit */
export async function editLinkGoto(
  body: API.LinkGotoEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkGoto/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /linkGoto/get/vo */
export async function getLinkGotoVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkGotoVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkGotoVO>('/linkGoto/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkGoto/list/page */
export async function listLinkGotoByPage(
  body: API.LinkGotoQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkGoto>('/linkGoto/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkGoto/list/page/vo */
export async function listLinkGotoVoByPage(
  body: API.LinkGotoQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkGotoVO>('/linkGoto/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkGoto/my/list/page/vo */
export async function listMyLinkGotoVoByPage(
  body: API.LinkGotoQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkGotoVO>('/linkGoto/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkGoto/update */
export async function updateLinkGoto(
  body: API.LinkGotoUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkGoto/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
