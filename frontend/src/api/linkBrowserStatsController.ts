// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /linkBrowserStats/add */
export async function addLinkBrowserStats(
  body: API.LinkBrowserStatsAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultLong>('/linkBrowserStats/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkBrowserStats/delete */
export async function deleteLinkBrowserStats(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkBrowserStats/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkBrowserStats/edit */
export async function editLinkBrowserStats(
  body: API.LinkBrowserStatsEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkBrowserStats/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /linkBrowserStats/get/vo */
export async function getLinkBrowserStatsVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkBrowserStatsVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkBrowserStatsVO>('/linkBrowserStats/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkBrowserStats/list/page */
export async function listLinkBrowserStatsByPage(
  body: API.LinkBrowserStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkBrowserStats>('/linkBrowserStats/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkBrowserStats/list/page/vo */
export async function listLinkBrowserStatsVoByPage(
  body: API.LinkBrowserStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkBrowserStatsVO>('/linkBrowserStats/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkBrowserStats/my/list/page/vo */
export async function listMyLinkBrowserStatsVoByPage(
  body: API.LinkBrowserStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkBrowserStatsVO>('/linkBrowserStats/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkBrowserStats/update */
export async function updateLinkBrowserStats(
  body: API.LinkBrowserStatsUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkBrowserStats/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
