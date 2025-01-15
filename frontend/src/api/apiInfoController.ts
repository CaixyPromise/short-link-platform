// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /api-key/query */
export async function queryApiKey(body: API.QueryApiKeyRequest, options?: { [key: string]: any }) {
  return request<API.ResultApiKeyVO>('/api-key/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /api-key/refresh */
export async function refreshApiKey(
  body: API.QueryApiKeyRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultApiKeyVO>('/api-key/refresh', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
