package com.caixy.shortlink.mapper;

import com.caixy.shortlink.model.dto.file.ReferenceWithFileInfoDTO;
import com.caixy.shortlink.model.entity.FileReference;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【t_file_reference(文件引用表)】的数据库操作Mapper
* @createDate 2025-04-22 19:15:08
* @Entity com.caixy.shortlink.model.entity.FileReference
*/
public interface FileReferenceMapper extends BaseMapper<FileReference> {
    /**
     * 查询某业务下用户的文件引用 + 文件信息列表
     * 
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/23 3:44
     */
    List<ReferenceWithFileInfoDTO> listReferenceWithFileInfo(@Param("userId") Long userId,
                                                             @Param("bizType") String bizType,
                                                             @Param("bizId") Long bizId);

}




