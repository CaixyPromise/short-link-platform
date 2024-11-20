// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 GET /captcha/get */
export async function getCaptcha(options?: { [key: string]: any }) {
  return request<API.ResultCaptchaVO>('/captcha/get', {
    method: 'GET',
    ...(options || {}),
  });
}
