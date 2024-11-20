// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /linkAccessLogs/add */
export async function addLinkAccessLogs(
  body: API.LinkAccessLogsAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultLong>('/linkAccessLogs/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessLogs/delete */
export async function deleteLinkAccessLogs(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkAccessLogs/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessLogs/edit */
export async function editLinkAccessLogs(
  body: API.LinkAccessLogsEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkAccessLogs/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /linkAccessLogs/get/vo */
export async function getLinkAccessLogsVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkAccessLogsVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkAccessLogsVO>('/linkAccessLogs/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessLogs/list/page */
export async function listLinkAccessLogsByPage(
  body: API.LinkAccessLogsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkAccessLogs>('/linkAccessLogs/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessLogs/list/page/vo */
export async function listLinkAccessLogsVoByPage(
  body: API.LinkAccessLogsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkAccessLogsVO>('/linkAccessLogs/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessLogs/my/list/page/vo */
export async function listMyLinkAccessLogsVoByPage(
  body: API.LinkAccessLogsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkAccessLogsVO>('/linkAccessLogs/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessLogs/update */
export async function updateLinkAccessLogs(
  body: API.LinkAccessLogsUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkAccessLogs/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
