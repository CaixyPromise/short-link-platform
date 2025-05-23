package com.caixy.shortlink.manager.file.strategy;

import com.caixy.shortlink.model.dto.file.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @name: com.caixy.shortlink.manager.file.strategy.FileActionStrategy
 * @description: 文件上传操作接口类
 * @author: CAIXYPROMISE
 * @date: 2024-05-22 16:51
 **/
public interface FileActionStrategy
{
    /**
     * 文件上传后处理操作
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/6/10 下午11:51
     */
    FileUploadAfterActionResult doAfterUploadAction(UploadFileDTO uploadFileDTO, Path savePath, UploadFileRequest uploadFileRequest,
                                                    HttpServletRequest request) throws IOException;

    /**
     * 文件上传前处理操作
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/6/10 下午11:51
     */
    default FileUploadBeforeActionResult doBeforeUploadAction(UploadFileDTO uploadFileDTO
            , UploadFileRequest uploadFileRequest)
    {
        return FileUploadBeforeActionResult.success();
    }

    /**
     * 文件下载前处理，默认禁止下载
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/7/2 下午5:07
     */
    default Boolean doBeforeDownloadAction(DownloadFileDTO downloadFileDTO)
    {
        return false;
    }

    /**
     * 文件下载后处理，默认无后处理
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/7/2 下午5:07
     */
    default Boolean doAfterDownloadAction(DownloadFileDTO downloadFileDTO)
    {
        return true;
    }
}
