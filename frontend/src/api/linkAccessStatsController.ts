// @ts-ignore
/* eslint-disable */
import request from '@/lib/request';

/** 此处后端没有提供注释 GET /access-stats/access-logs */
export async function shortLinkStatsAccessRecord(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.shortLinkStatsAccessRecordParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultIPageShortLinkStatsAccessRecordRespDTO>('/access-stats/access-logs', {
    method: 'GET',
    params: {
      ...params,
      requestParam: undefined,
      ...params['requestParam'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /access-stats/access-logs/group */
export async function groupShortLinkStatsAccessRecord(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.groupShortLinkStatsAccessRecordParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultIPageShortLinkStatsAccessRecordRespDTO>(
    '/access-stats/access-logs/group',
    {
      method: 'GET',
      params: {
        ...params,
        requestParam: undefined,
        ...params['requestParam'],
      },
      ...(options || {}),
    },
  );
}

/** 此处后端没有提供注释 GET /access-stats/group */
export async function groupShortLinkStats(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.groupShortLinkStatsParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultShortLinkStatsRespDTO>('/access-stats/group', {
    method: 'GET',
    params: {
      ...params,
      requestParam: undefined,
      ...params['requestParam'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /access-stats/one */
export async function shortLinkStats(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.shortLinkStatsParams,
  options?: { [key: string]: any },
) {
  return request<API.ResultShortLinkStatsRespDTO>('/access-stats/one', {
    method: 'GET',
    params: {
      ...params,
      requestParam: undefined,
      ...params['requestParam'],
    },
    ...(options || {}),
  });
}
