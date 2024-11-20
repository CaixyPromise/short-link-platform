// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /linkDeviceStats/add */
export async function addLinkDeviceStats(
  body: API.LinkDeviceStatsAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultLong>('/linkDeviceStats/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkDeviceStats/delete */
export async function deleteLinkDeviceStats(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkDeviceStats/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkDeviceStats/edit */
export async function editLinkDeviceStats(
  body: API.LinkDeviceStatsEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkDeviceStats/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /linkDeviceStats/get/vo */
export async function getLinkDeviceStatsVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkDeviceStatsVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkDeviceStatsVO>('/linkDeviceStats/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkDeviceStats/list/page */
export async function listLinkDeviceStatsByPage(
  body: API.LinkDeviceStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkDeviceStats>('/linkDeviceStats/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkDeviceStats/list/page/vo */
export async function listLinkDeviceStatsVoByPage(
  body: API.LinkDeviceStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkDeviceStatsVO>('/linkDeviceStats/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkDeviceStats/my/list/page/vo */
export async function listMyLinkDeviceStatsVoByPage(
  body: API.LinkDeviceStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkDeviceStatsVO>('/linkDeviceStats/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkDeviceStats/update */
export async function updateLinkDeviceStats(
  body: API.LinkDeviceStatsUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkDeviceStats/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
