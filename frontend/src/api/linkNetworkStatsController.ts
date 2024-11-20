// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /linkNetworkStats/add */
export async function addLinkNetworkStats(
  body: API.LinkNetworkStatsAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultLong>('/linkNetworkStats/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkNetworkStats/delete */
export async function deleteLinkNetworkStats(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkNetworkStats/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkNetworkStats/edit */
export async function editLinkNetworkStats(
  body: API.LinkNetworkStatsEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkNetworkStats/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /linkNetworkStats/get/vo */
export async function getLinkNetworkStatsVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkNetworkStatsVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkNetworkStatsVO>('/linkNetworkStats/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkNetworkStats/list/page */
export async function listLinkNetworkStatsByPage(
  body: API.LinkNetworkStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkNetworkStats>('/linkNetworkStats/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkNetworkStats/list/page/vo */
export async function listLinkNetworkStatsVoByPage(
  body: API.LinkNetworkStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkNetworkStatsVO>('/linkNetworkStats/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkNetworkStats/my/list/page/vo */
export async function listMyLinkNetworkStatsVoByPage(
  body: API.LinkNetworkStatsQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkNetworkStatsVO>('/linkNetworkStats/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkNetworkStats/update */
export async function updateLinkNetworkStats(
  body: API.LinkNetworkStatsUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkNetworkStats/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
