// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 GET /file/check_exist */
export async function checkFileExist(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.checkFileExistParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultCheckFileExistResponse>('/file/check_exist', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /file/download */
export async function downloadFileById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.downloadFileByIdParams,
  options?: { [key: string]: any },
) {
  return request<any>('/file/download', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /file/upload */
export async function uploadFile(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadFileParams,
  body: {},
  options?: { [key: string]: any },
) {
  return request<API.ResultString>('/file/upload', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: {
      ...params,
      uploadFileRequest: undefined,
      ...params['uploadFileRequest'],
    },
    data: body,
    ...(options || {}),
  });
}
