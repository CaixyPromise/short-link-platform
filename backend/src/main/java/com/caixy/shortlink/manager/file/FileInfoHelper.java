package com.caixy.shortlink.manager.file;

import com.caixy.shortlink.model.entity.FileReference;
import com.caixy.shortlink.service.FileInfoService;
import com.caixy.shortlink.service.FileReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件操作服务类，用于暴露给前后处理操作的业务类进行调用
 *
 * @Author CAIXYPROMISE
 * @since 2025/4/23 3:06
 */
@Component
@RequiredArgsConstructor
public class FileInfoHelper
{
    private final FileInfoService fileInfoService;
    private final FileReferenceService fileReferenceService;

    /**
     * 删除文件 == 删除文件引用
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/23 3:10
     */
    public Boolean removeFile(Long fileId, Long userId, String bizType, Long bizId) {
        return fileReferenceService.removeFileReferenceById(fileId, userId, bizType, bizId);
    }
    public Boolean batchRemoveByIds(List<Long> fileIds, Long userId, String bizType, Long bizId) {
        return fileReferenceService.removeFileReferencesByFileIds(fileIds, userId, bizType, bizId);
    }



    public List<FileReference> listFileByBiz(Long userId, String bizType, Long bizId) {
        return fileReferenceService.listFileReferenceByBiz(userId, bizType, bizId);
    }



}
