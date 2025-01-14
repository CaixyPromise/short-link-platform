// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 POST /feedback/page/my */
export async function getMyFeedbackPage(
  body: API.PageMyFeedbackRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultIPageUserFeedbackInfoVO>('/feedback/page/my', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /feedback/post */
export async function postFeedback(
  body: API.PostFeedbackRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultLong>('/feedback/post', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
