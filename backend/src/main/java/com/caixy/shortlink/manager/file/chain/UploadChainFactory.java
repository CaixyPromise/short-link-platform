package com.caixy.shortlink.manager.file.chain;

import com.caixy.shortlink.common.chain.ChainExecutor;
import com.caixy.shortlink.manager.file.chain.handler.*;
import com.caixy.shortlink.manager.file.domain.UploadContext;
import com.caixy.shortlink.manager.file.strategy.UploadFileMethodStrategy;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.service.FileInfoService;
import com.caixy.shortlink.service.FileReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 上传业务责任链工厂
 *
 * @Author CAIXYPROMISE
 * @since 2025/5/27 2:09
 */
@Component
@RequiredArgsConstructor
public class UploadChainFactory
{
    private final FileInfoService fileInfoService;
    private final FileReferenceService fileReferenceService;

    /**
     * 秒传业务逻辑
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/5/29 3:25
     */
    public String doFasterUpload(UploadContext uploadContext)
    {
        ChainExecutor<UploadContext, String> executor = new ChainExecutor<>();
        executor.addHandler(new FasterUploadPrepareHandler())
                .addHandler(new FasterUploadValidHandler())
                .addHandler(new DoBeforeActionHandler())
                .addHandler(new DoAfterActionHandler())
                .addHandler(new SystemAfterActionHandler(fileReferenceService));
        return executor.execute(uploadContext);
    }

    /**
     * 普通上传逻辑
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/5/29 3:25
     */
    public String doNormalUpload(UploadContext uploadContext)
    {
        UploadFileMethodStrategy uploadFileMethodStrategy = uploadContext.getUploadFileMethodStrategy();
        ChainExecutor<UploadContext, String> executor = new ChainExecutor<>();
        executor.addHandler(new NormalUploadPrepareHandler())
                .addHandler(new DoBeforeActionHandler())
                .addHandler(new DoSaveFileHandler(uploadFileMethodStrategy, fileInfoService))
                .addHandler(new DoAfterActionHandler())
                .addHandler(new SystemAfterActionHandler(fileReferenceService));
        return executor.execute(uploadContext);
    }
}
