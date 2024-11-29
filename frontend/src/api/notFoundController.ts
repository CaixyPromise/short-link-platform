// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 GET /page/notFound */
export async function notFound(options?: { [key: string]: any }) {
  return request<API.ResultString>('/page/notFound', {
    method: 'GET',
    ...(options || {}),
  });
}
