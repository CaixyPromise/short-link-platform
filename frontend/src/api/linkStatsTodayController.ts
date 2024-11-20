// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /linkStatsToday/add */
export async function addLinkStatsToday(
  body: API.LinkStatsTodayAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultLong>('/linkStatsToday/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkStatsToday/delete */
export async function deleteLinkStatsToday(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkStatsToday/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkStatsToday/edit */
export async function editLinkStatsToday(
  body: API.LinkStatsTodayEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkStatsToday/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /linkStatsToday/get/vo */
export async function getLinkStatsTodayVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLinkStatsTodayVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkStatsTodayVO>('/linkStatsToday/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkStatsToday/list/page */
export async function listLinkStatsTodayByPage(
  body: API.LinkStatsTodayQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkStatsToday>('/linkStatsToday/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkStatsToday/list/page/vo */
export async function listLinkStatsTodayVoByPage(
  body: API.LinkStatsTodayQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkStatsTodayVO>('/linkStatsToday/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkStatsToday/my/list/page/vo */
export async function listMyLinkStatsTodayVoByPage(
  body: API.LinkStatsTodayQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageLinkStatsTodayVO>('/linkStatsToday/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /linkStatsToday/update */
export async function updateLinkStatsToday(
  body: API.LinkStatsTodayUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultBoolean>('/linkStatsToday/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
