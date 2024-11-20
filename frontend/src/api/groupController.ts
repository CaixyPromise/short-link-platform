// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /group/add */
export async function addGroup(body: API.GroupAddRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/group/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /group/delete */
export async function deleteGroup(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/group/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /group/edit */
export async function editGroup(body: API.GroupEditRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/group/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /group/get/group/item */
export async function getMyGroupItems(options?: { [key: string]: any }) {
  return request<API.ResultListGroupItemVO>('/group/get/group/item', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /group/get/vo */
export async function getGroupVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getGroupVOByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultGroupVO>('/group/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /group/list/page */
export async function listGroupByPage(
  body: API.GroupQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageGroup>('/group/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /group/list/page/vo */
export async function listGroupVoByPage(
  body: API.GroupQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageGroupVO>('/group/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /group/my/list/page/vo */
export async function listMyGroupVoByPage(
  body: API.GroupQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultPageGroupVO>('/group/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /group/update */
export async function updateGroup(body: API.GroupUpdateRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/group/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
