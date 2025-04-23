package com.caixy.shortlink.service;

import com.caixy.shortlink.model.dto.file.FileUploadAfterActionResult;
import com.caixy.shortlink.model.entity.FileReference;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.enums.FileActionBizEnum;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【t_file_reference(文件引用表)】的数据库操作Service
* @createDate 2025-04-22 19:15:08
*/
public interface FileReferenceService extends IService<FileReference> {

    Boolean bindFileReference(Long fileId, Long userId, FileActionBizEnum fileActionBizEnum, FileUploadAfterActionResult afterActionResult);

    Boolean removeFileReferenceById(Long fileId, Long userId, String bizType, Long bizId);

    Boolean removeFileReferenceByBiz(Long userId, String bizType, Long bizId);

    Boolean removeFileReferencesByFileIds(List<Long> fileIds, Long userId, String bizType, Long bizId);

    List<FileReference> listFileReferenceByBiz(Long userId, String bizType, Long bizId);
}
