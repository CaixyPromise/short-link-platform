package com.caixy.shortlink.common;

/**
 * 返回工具类
 */
public class ResultUtils
{

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data)
    {
        return new Result<>(0, data, "ok");
    }

    /**
     * failure是给，请求成功，但是系统操作失败的'温柔'响应。
     * 
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/2/13 0:58
     */
    public static <T> Result<T> failure(T data)
    {
        return new Result<>(ErrorCode.FAILURE.getCode(), data, ErrorCode.FAILURE.getMessage());
    }

    public static <T> Result<T> failure(ErrorCode errorCode, T data)
    {
        // 如果状态码是成功，则直接调用成功的返回结构
        if (errorCode.equals(ErrorCode.SUCCESS))
        {
            return success(data);
        }
        return new Result<>(errorCode.getCode(), data, errorCode.getMessage());
    }

    public static <T> Result<T> failure(ErrorCode errorCode, T data, String message)
    {
        if (errorCode.equals(ErrorCode.SUCCESS))
        {
            return success(data);
        }
        return new Result<>(errorCode.getCode(), data, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static Result error(ErrorCode errorCode)
    {
        return new Result<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static Result error(int code, String message)
    {
        return new Result(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static Result error(ErrorCode errorCode, String message)
    {
        return new Result(errorCode.getCode(), null, message);
    }
}
