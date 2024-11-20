// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /linkLocaleStats/add */
export async function addLinkLocaleStats(
  body: API.LinkLocaleStatsAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultLong>('/linkLocaleStats/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkLocaleStats/delete */
export async function deleteLinkLocaleStats(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkLocaleStats/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkLocaleStats/edit */
export async function editLinkLocaleStats(
  body: API.LinkLocaleStatsEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkLocaleStats/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /linkLocaleStats/get/vo */
export async function getLinkLocaleStatsVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkLocaleStatsVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkLocaleStatsVO>('/linkLocaleStats/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkLocaleStats/list/page */
export async function listLinkLocaleStatsByPage(
  body: API.LinkLocaleStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkLocaleStats>('/linkLocaleStats/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkLocaleStats/list/page/vo */
export async function listLinkLocaleStatsVoByPage(
  body: API.LinkLocaleStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkLocaleStatsVO>('/linkLocaleStats/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkLocaleStats/my/list/page/vo */
export async function listMyLinkLocaleStatsVoByPage(
  body: API.LinkLocaleStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkLocaleStatsVO>('/linkLocaleStats/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkLocaleStats/update */
export async function updateLinkLocaleStats(
  body: API.LinkLocaleStatsUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkLocaleStats/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
