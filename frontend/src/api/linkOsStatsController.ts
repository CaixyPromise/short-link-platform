// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /linkOsStats/add */
export async function addLinkOsStats(
  body: API.LinkOsStatsAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultLong>('/linkOsStats/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkOsStats/delete */
export async function deleteLinkOsStats(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/linkOsStats/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkOsStats/edit */
export async function editLinkOsStats(
  body: API.LinkOsStatsEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkOsStats/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /linkOsStats/get/vo */
export async function getLinkOsStatsVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkOsStatsVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkOsStatsVO>('/linkOsStats/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkOsStats/list/page */
export async function listLinkOsStatsByPage(
  body: API.LinkOsStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkOsStats>('/linkOsStats/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkOsStats/list/page/vo */
export async function listLinkOsStatsVoByPage(
  body: API.LinkOsStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkOsStatsVO>('/linkOsStats/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkOsStats/my/list/page/vo */
export async function listMyLinkOsStatsVoByPage(
  body: API.LinkOsStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkOsStatsVO>('/linkOsStats/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkOsStats/update */
export async function updateLinkOsStats(
  body: API.LinkOsStatsUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkOsStats/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
