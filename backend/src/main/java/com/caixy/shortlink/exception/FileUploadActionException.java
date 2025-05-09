package com.caixy.shortlink.exception;

import com.caixy.shortlink.common.ErrorCode;
import lombok.Getter;

/**
 * 文件上传操作异常类
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.exception.FileUploadActionException
 * @since 2024-06-12 12:43
 **/
@Getter
public class FileUploadActionException extends RuntimeException
{
    private final ErrorCode errorCode;
    private final String message;

    public FileUploadActionException(ErrorCode errorCode)
    {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public FileUploadActionException(ErrorCode errorCode, String message)
    {
        this.errorCode = errorCode;
        this.message = message;
    }
}
