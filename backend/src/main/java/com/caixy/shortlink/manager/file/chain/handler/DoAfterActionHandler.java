package com.caixy.shortlink.manager.file.chain.handler;

import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.chain.ChainContext;
import com.caixy.shortlink.common.chain.ChainHandler;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.file.domain.UploadContext;
import com.caixy.shortlink.manager.file.strategy.FileActionStrategy;
import com.caixy.shortlink.model.dto.file.FileUploadAfterActionResult;

/**
 * 业务后置校验
 *
 * @Author CAIXYPROMISE
 * @since 2025/5/27 1:29
 */
public class DoAfterActionHandler implements ChainHandler<UploadContext, String>
{
    @Override
    public void handle(ChainContext<UploadContext, String> context)
    {
        UploadContext contextData = context.getData();
        FileActionStrategy fileActionStrategy = contextData.getFileActionStrategy();
        FileUploadAfterActionResult afterActionResult = fileActionStrategy.doAfterUploadAction(
                contextData,
                contextData.getFileActionHelper(),
                contextData.getSavePath(),
                contextData.getUploadFileRequest(),
                contextData.getRequest());
        if (!afterActionResult.getSuccess()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        contextData.setAfterActionResult(afterActionResult);
    }
}
