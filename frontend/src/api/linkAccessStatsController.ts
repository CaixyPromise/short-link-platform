// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /linkAccessStats/add */
export async function addLinkAccessStats(
  body: API.LinkAccessStatsAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultLong>('/linkAccessStats/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessStats/delete */
export async function deleteLinkAccessStats(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkAccessStats/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessStats/edit */
export async function editLinkAccessStats(
  body: API.LinkAccessStatsEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkAccessStats/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /linkAccessStats/get/vo */
export async function getLinkAccessStatsVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkAccessStatsVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkAccessStatsVO>('/linkAccessStats/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessStats/list/page */
export async function listLinkAccessStatsByPage(
  body: API.LinkAccessStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkAccessStats>('/linkAccessStats/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessStats/list/page/vo */
export async function listLinkAccessStatsVoByPage(
  body: API.LinkAccessStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkAccessStatsVO>('/linkAccessStats/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessStats/my/list/page/vo */
export async function listMyLinkAccessStatsVoByPage(
  body: API.LinkAccessStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkAccessStatsVO>('/linkAccessStats/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkAccessStats/update */
export async function updateLinkAccessStats(
  body: API.LinkAccessStatsUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkAccessStats/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
