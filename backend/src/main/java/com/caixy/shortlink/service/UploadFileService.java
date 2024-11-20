package com.caixy.shortlink.service;

import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileRequest;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.model.enums.SaveFileMethodEnum;
import com.caixy.shortlink.strategy.FileActionStrategy;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @name: com.caixy.shortlink.service.UploadFileService
 * @description: 文件上传下载服务
 * @author: CAIXYPROMISE
 * @date: 2024-05-21 21:52
 **/
public interface UploadFileService
{
    org.springframework.core.io.Resource getFile(FileActionBizEnum fileActionBizEnum, Path filePath) throws IOException;

    void deleteFile(FileActionBizEnum fileActionBizEnum, Path filePath);

    void deleteFile(FileActionBizEnum fileActionBizEnum, Long userId, String filename);

    Path saveFile(UploadFileDTO uploadFileDTO) throws IOException;

    FileActionStrategy getFileActionService(FileActionBizEnum fileActionBizEnum);

    String handleUpload(UploadFileRequest uploadFileRequest, FileActionBizEnum uploadBizEnum,
                        SaveFileMethodEnum saveFileMethod, UploadFileDTO uploadFileDTO, HttpServletRequest request);
}
