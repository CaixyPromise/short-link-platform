package com.caixy.shortlink.service;

import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileRequest;
import com.caixy.shortlink.model.entity.FileInfo;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.model.enums.SaveFileMethodEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.manager.file.strategy.FileActionStrategy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * @name: com.caixy.shortlink.service.UploadFileService
 * @description: 文件上传下载服务
 * @author: CAIXYPROMISE
 * @date: 2024-05-21 21:52
 **/
public interface UploadFileService
{
    /**
     * 生成上传文件token，在判断文件存在时创建的
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/22 19:45
     */
    String generateUploadFileToken(String sha256, Long fileSize, UserVO userInfo, FileInfo fileInfo);

    Map<String, Object> parasTokenInfoMap(String token);

    Resource getFile(FileActionBizEnum fileActionBizEnum, Path filePath) throws IOException;

    void deleteFile(FileActionBizEnum fileActionBizEnum, Path filePath);

    void deleteFile(FileActionBizEnum fileActionBizEnum, Long userId, String filename);

    Path saveFile(UploadFileDTO uploadFileDTO) throws IOException;

    FileActionStrategy getFileActionService(FileActionBizEnum fileActionBizEnum);

    String handleUpload(UploadFileRequest uploadFileRequest, FileActionBizEnum uploadBizEnum,
                        SaveFileMethodEnum saveFileMethod, UploadFileDTO uploadFileDTO, HttpServletRequest request);
}
