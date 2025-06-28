package com.caixy.shortlink.manager.file.chain.handler;

import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.chain.ChainContext;
import com.caixy.shortlink.common.chain.ChainHandler;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.file.domain.UploadContext;
import com.caixy.shortlink.manager.file.strategy.FileActionStrategy;
import com.caixy.shortlink.model.dto.file.FileUploadBeforeActionResult;

/**
 * 业务前置校验（权限、配额等）
 *
 * @Author CAIXYPROMISE
 * @since 2025/5/27 1:29
 */
public class DoBeforeActionHandler implements ChainHandler<UploadContext, String>
{

    @Override
    public void handle(ChainContext<UploadContext, String> context)
    {
        UploadContext contextData = context.getData();
        FileActionStrategy fileActionStrategy = contextData.getFileActionStrategy();
        FileUploadBeforeActionResult fileUploadBeforeActionResult = fileActionStrategy.doBeforeUploadAction(contextData, contextData.getFileActionHelper(), contextData.getUploadFileRequest(), contextData.getRequest());
        if (!fileUploadBeforeActionResult.getSuccess()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        contextData.setBeforeActionResult(fileUploadBeforeActionResult);
    }
}
