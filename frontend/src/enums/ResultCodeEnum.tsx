
export enum ResultCode {
	SUCCESS = 0,
	PARAMS_ERROR = 40000,
	NOT_LOGIN_ERROR = 40100,
	NO_AUTH_ERROR = 40101,
	NOT_FOUND_ERROR = 40400,
	FORBIDDEN_ERROR = 40300,
	SYSTEM_ERROR = 50000,
	OPERATION_ERROR = 50001
}

// 错误信息映射对象
const ErrorMessages: Record<ResultCode, string> = {
	[ResultCode.SUCCESS]: "ok",
	[ResultCode.PARAMS_ERROR]: "请求参数错误",
	[ResultCode.NOT_LOGIN_ERROR]: "未登录",
	[ResultCode.NO_AUTH_ERROR]: "无权限",
	[ResultCode.NOT_FOUND_ERROR]: "请求数据不存在",
	[ResultCode.FORBIDDEN_ERROR]: "禁止访问",
	[ResultCode.SYSTEM_ERROR]: "系统内部异常",
	[ResultCode.OPERATION_ERROR]: "操作失败"
};

// 获取错误信息的函数
export function getErrorMessage(code: ResultCode): string {
	return ErrorMessages[code] || "未知错误";
}
