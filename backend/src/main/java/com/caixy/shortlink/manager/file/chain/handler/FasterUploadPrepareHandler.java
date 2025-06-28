package com.caixy.shortlink.manager.file.chain.handler;

import cn.hutool.core.io.FileUtil;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.chain.ChainContext;
import com.caixy.shortlink.common.chain.ChainHandler;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.file.domain.UploadContext;
import com.caixy.shortlink.manager.file.strategy.FileActionStrategy;
import com.caixy.shortlink.manager.file.strategy.UploadFileMethodStrategy;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileRequest;
import com.caixy.shortlink.model.entity.FileInfo;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.service.FileReferenceService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

/**
 * 秒传前置处理器
 *
 * @Author CAIXYPROMISE
 * @since 2025/5/29 3:52
 */
@RequiredArgsConstructor
public class FasterUploadPrepareHandler implements ChainHandler<UploadContext, String>
{
    @Override
    public void handle(ChainContext<UploadContext, String> context)
    {
        UploadContext contextData = context.getData();
        FileInfo fileInfo = contextData.getFileInfo();
        if (fileInfo == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件不存在，无法秒传");
        }
        contextData.setFileInfo(fileInfo);
        UploadFileRequest req = contextData.getUploadFileRequest();
        Long userId = contextData.getUserId();
        FileActionBizEnum bizEnum = req.getBiz();

        // 推断文件后缀
        String fileRealName = req.getFileName();
        String fileSuffix = FileUtil.getSuffix(fileRealName);

        UploadFileMethodStrategy uploadFileMethodStrategy = contextData.getUploadFileMethodStrategy();

        // 构造FileSaveInfo
        UploadFileDTO.FileSaveInfo fileSaveInfo = UploadFileDTO.FileSaveInfo.builder()
            .fileInnerName(fileInfo.getFileInnerName())
            .fileRealName(fileRealName)
            .fileSuffix(fileSuffix)
            .filePath(Path.of(fileInfo.getStoragePath()))
            .fileURL(uploadFileMethodStrategy.buildFileURL(userId, fileInfo.getFileInnerName(), bizEnum))
            .contentType(fileInfo.getContentType())
            .build();

        // 构造UploadFileDTO
        UploadFileDTO uploadFileDTO = UploadFileDTO.builder()
            .userId(userId)
            .fileActionBizEnum(bizEnum)
            .fileSaveInfo(fileSaveInfo)
            .sha256(fileInfo.getFileSha256())
            .fileSize(fileInfo.getFileSize())
            .build();

        contextData.setUploadFileDTO(uploadFileDTO);
    }
}
