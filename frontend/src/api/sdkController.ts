// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /sdk/test */
export async function sdkTest(
  body: {
    addLinkSdkRequest?: API.LinkAddRequest;
    userVO?: API.UserVO;
  },
  options?: { [key: string]: any },
) {
  return request<API.ResultLinkCreateVO>('/sdk/test', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
