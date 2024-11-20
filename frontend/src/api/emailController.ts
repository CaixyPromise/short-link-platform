// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /email/send */
export async function sendEmail(body: API.SendEmailRequest, options?: { [key: string]: any }) {
  return request<API.ResultBoolean>('/email/send', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
