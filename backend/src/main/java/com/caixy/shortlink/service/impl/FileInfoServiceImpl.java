package com.caixy.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.model.entity.FileInfo;
import com.caixy.shortlink.service.FileInfoService;
import com.caixy.shortlink.mapper.FileInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @author CAIXYPROMISE
 * @description 针对表【t_file_info(文件信息表)】的数据库操作Service实现
 * @createDate 2025-04-22 19:15:08
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService
{

    @Override
    public FileInfo findFileBySha256AndSize(String sha256, Long fileSize)
    {
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileInfo::getFileSha256, sha256);
        lambdaQueryWrapper.eq(FileInfo::getFileSize, fileSize);
        return this.getOne(lambdaQueryWrapper);
    }
}




