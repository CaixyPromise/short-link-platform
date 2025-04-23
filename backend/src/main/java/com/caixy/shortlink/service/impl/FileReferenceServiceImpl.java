package com.caixy.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.model.dto.file.FileUploadAfterActionResult;
import com.caixy.shortlink.model.entity.FileReference;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.service.FileReferenceService;
import com.caixy.shortlink.mapper.FileReferenceMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【t_file_reference(文件引用表)】的数据库操作Service实现
 * @createDate 2025-04-22 19:15:08
 */
@Service
public class FileReferenceServiceImpl extends ServiceImpl<FileReferenceMapper, FileReference> implements FileReferenceService
{
    @Override
    public Boolean bindFileReference(Long fileId, Long userId, FileActionBizEnum fileActionBizEnum, FileUploadAfterActionResult afterActionResult)
    {
        // 1. 检查数据完整性
        if (fileId == null || userId == null || fileActionBizEnum == null || afterActionResult == null)
        {
            return false;
        }
        // 2. 构建文件引用对象
        FileReference fileReference = FileReference.builder().fileId(fileId).bizId(afterActionResult.getBizId()).userId(userId).accessLevel(afterActionResult.getAccessLevelEnum().getCode()).bizType(fileActionBizEnum.getLabel()).displayName(afterActionResult.getDisplayName()).build();
        return this.save(fileReference);
    }

    /**
     * 根据文件id删除，用于强关联场景
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/23 3:14
     */
    @Override
    public Boolean removeFileReferenceById(Long fileId, Long userId, String bizType, Long bizId)
    {
        return this.remove(buildQueryWrapper(fileId, userId, bizType, bizId));
    }

    /**
     * 根据业务类型删除: 注意，如果业务下每个用户公用一个业务信息id，这个方法会删除所有引用。
     * 比如，存储帖子场景，删除业务为帖子，bizId是帖子id，这个场景下会删除完所有帖子的引用。
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/23 3:15
     */
    @Override
    public Boolean removeFileReferenceByBiz(Long userId, String bizType, Long bizId)
    {
        return this.remove(buildQueryWrapper(null, userId, bizType, bizId));
    }

    /**
     * 按 fileId 列表批量删除引用
     * 
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/23 4:16
     */
    @Override
    public Boolean removeFileReferencesByFileIds(List<Long> fileIds, Long userId, String bizType, Long bizId) {
        if (fileIds == null || fileIds.isEmpty()) {
            return false;
        }

        return this.lambdaUpdate()
                .eq(FileReference::getUserId, userId)
                .eq(FileReference::getBizType, bizType)
                .eq(FileReference::getBizId, bizId)
                .in(FileReference::getFileId, fileIds)
                .remove();
    }


    /**
     * 返回用户在指定业务+id下关联的文件。
     * 
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/23 3:25
     */
    @Override
    public List<FileReference> listFileReferenceByBiz(Long userId, String bizType, Long bizId) {
        return this.lambdaQuery()
                .eq(FileReference::getUserId, userId)
                .eq(FileReference::getBizType, bizType)
                .eq(FileReference::getBizId, bizId)
                .eq(FileReference::getIsDeleted, 0)
                .list();
    }


    private LambdaQueryWrapper<FileReference> buildQueryWrapper(Long fileId, Long userId, String bizType, Long bizId)
    {
        LambdaQueryWrapper<FileReference> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(fileId != null, FileReference::getFileId, fileId)
                .eq(FileReference::getUserId, userId)
                .eq(FileReference::getBizType, bizType)
                .eq(FileReference::getBizId, bizId);
        return queryWrapper;
    }

}




