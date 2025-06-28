// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 GET /file/check */
export async function checkFileExist(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.checkFileExistParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultCheckFileExistResponse>('/file/check', {
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

/** 上传文件 POST /file/upload */
export async function uploadFile(
  body: {
    uploadFileRequest: API.UploadFileRequest;
  },
  file?: File,
  options?: { [key: string]: any },
) {
  const formData = new FormData();

  if (file) {
    formData.append('file', file);
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele];

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''));
        } else {
          formData.append(ele, JSON.stringify(item));
        }
      } else {
        formData.append(ele, item);
      }
    }
  });

  return request<API.ResultString>('/file/upload', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /file/upload/faster */
export async function uploadFileFaster(
  body: API.UploadFileRequest,
  options?: { [key: string]: any },
) {
  return request<API.ResultString>('/file/upload/faster', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
