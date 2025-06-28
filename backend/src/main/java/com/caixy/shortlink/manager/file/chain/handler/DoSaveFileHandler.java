package com.caixy.shortlink.manager.file.chain.handler;

import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.chain.ChainContext;
import com.caixy.shortlink.common.chain.ChainHandler;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.file.FileActionHelper;
import com.caixy.shortlink.manager.file.domain.UploadContext;
import com.caixy.shortlink.manager.file.strategy.UploadFileMethodStrategy;
import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.entity.FileInfo;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.service.FileInfoService;
import com.caixy.shortlink.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 文件持久化存储
 *
 * @Author CAIXYPROMISE
 * @since 2025/5/27 1:22
 */
@RequiredArgsConstructor
public class DoSaveFileHandler implements ChainHandler<UploadContext, String>
{
    private static final Logger log = LoggerFactory.getLogger(DoSaveFileHandler.class);
    private final UploadFileMethodStrategy uploadFileMethodStrategy;
    private final FileInfoService fileInfoService;

    @Override
    public void handle(ChainContext<UploadContext, String> context)
    {
        // 先检查上传过文件
        UploadContext contextData = context.getData();
        UploadFileDTO uploadFileDTO = contextData.getUploadFileDTO();
        FileInfo checkExistFile = fileInfoService.findFileBySha256AndSize(uploadFileDTO.getSha256(), uploadFileDTO.getFileSize());
        if (checkExistFile != null)
        {
            // 如果文件存在，则直接返回
            log.warn("上传重复文件, 用户Id: {}, 文件Id: {}, 文件名: {}", contextData.getUserId(), checkExistFile.getId(), checkExistFile.getFileInnerName());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败");
        }
        FileActionBizEnum fileActionBizEnum = uploadFileDTO.getFileActionBizEnum();
        String visitUrl = uploadFileMethodStrategy.buildFileURL(contextData.getUserId(), uploadFileDTO.getFileSaveInfo().getFileInnerName(), fileActionBizEnum);
        // 设置访问地址
        uploadFileDTO.getFileSaveInfo().setFileURL(visitUrl);
        Path savePath = null;
        try
        {
            savePath = uploadFileMethodStrategy.saveFile(uploadFileDTO);
        }
        catch (IOException e)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
        if (savePath == null || StringUtils.isAnyBlank(visitUrl, savePath.toString()))
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
        // 设置保存地址
        contextData.setVisitUrl(visitUrl);
        // 保存文件到文件表里
        FileInfo fileInfo = FileInfo.builder().fileSha256(uploadFileDTO.getSha256()).fileSize(uploadFileDTO.getFileSize()).fileInnerName(uploadFileDTO.getFileSaveInfo().getFileInnerName()).contentType(uploadFileDTO.getFileSaveInfo().getContentType()).storagePath(savePath.toString()).storageType(uploadFileDTO.getFileActionBizEnum().getSaveFileMethod().getCode()).build();
        contextData.setFileInfo(fileInfo);
        boolean fileSaved = fileInfoService.save(fileInfo);
        if (!fileSaved)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }
}
