package com.caixy.shortlink.manager.file;

import com.caixy.shortlink.model.entity.FileReference;
import com.caixy.shortlink.service.FileInfoService;
import com.caixy.shortlink.service.FileReferenceService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件操作服务类，用于暴露给前后处理操作的业务类进行调用
 *
 * @Author CAIXYPROMISE
 * @since 2025/4/23 3:06
 */
@Getter
@Component
@RequiredArgsConstructor
public class FileActionHelper
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
    /**
     * 批量删除文件引用
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/5/26 16:51
     */
    public Boolean batchRemoveByIds(List<Long> fileIds, Long userId, String bizType, Long bizId) {
        return fileReferenceService.removeFileReferencesByFileIds(fileIds, userId, bizType, bizId);
    }

    /**
     * 根据用户Id和业务信息查询文件引用
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/23 3:10
     */
    public List<FileReference> listFileByBiz(Long userId, String bizType, Long bizId) {
        return fileReferenceService.listFileReferenceByBiz(userId, bizType, bizId);
    }

    public FileReference findFileReferenceByBiz(Long userId, String bizType, Long bizId) {
        return fileReferenceService.findFileReferenceByBiz(userId, bizType, bizId);
    }

    public Boolean isSameFile(String bizType, Long bizId, Long userId, String sha256, Long fileSize) {
        return fileReferenceService.isSameFile(bizType, bizId, userId, sha256, fileSize);
    }

}
