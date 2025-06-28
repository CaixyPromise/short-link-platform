package com.caixy.shortlink.manager.file.chain.handler;

import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.chain.ChainContext;
import com.caixy.shortlink.common.chain.ChainHandler;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.file.domain.UploadContext;
import com.caixy.shortlink.model.entity.FileInfo;
import com.caixy.shortlink.model.entity.FileReference;
import com.caixy.shortlink.service.FileReferenceService;
import lombok.RequiredArgsConstructor;

/**
 * 系统最后处理处理器：绑定文件引用等操作。
 *
 * @Author CAIXYPROMISE
 * @since 2025/5/27 1:22
 */
@RequiredArgsConstructor
public class SystemAfterActionHandler implements ChainHandler<UploadContext, String>
{
    private final FileReferenceService fileReferenceService;

    @Override
    public void handle(ChainContext<UploadContext, String> context)
    {
        UploadContext contextData = context.getData();
        FileInfo fileInfo = contextData.getFileInfo();
        FileReference fileReference = fileReferenceService.bindFileReference(fileInfo.getId(),
                contextData.getUserId(),
                contextData.getUploadFileDTO().getFileActionBizEnum(),
                contextData.getAfterActionResult());
        if (fileReference == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件引用绑定失败");
        }
        context.setResult(fileReference.getVisitUrl());
    }
}
