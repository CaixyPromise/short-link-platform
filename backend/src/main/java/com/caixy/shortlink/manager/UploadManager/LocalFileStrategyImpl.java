package com.caixy.shortlink.manager.UploadManager;

import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.manager.UploadManager.annotation.UploadMethodTarget;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.config.LocalFileConfig;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.file.strategy.UploadFileMethodStrategy;
import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.model.enums.SaveFileMethodEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件管理器
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.manager.uploadManager.LocalFileStrategyImpl
 * @since 2024-05-21 20:13
 **/
@Component
@AllArgsConstructor
@Slf4j
@UploadMethodTarget(SaveFileMethodEnum.SAVE_LOCAL)
public class LocalFileStrategyImpl implements UploadFileMethodStrategy
{
    private final LocalFileConfig localFileConfig;

    public Path saveFile(MultipartFile multipartFile, UploadFileDTO fileConfig)
    {
        UploadFileDTO.FileSaveInfo fileSaveInfo = fileConfig.getFileSaveInfo();
        String filename = fileSaveInfo.getFileInnerName(); // 文件名
        Path filePath = fileSaveInfo.getFilePath();
        FileActionBizEnum fileActionBizEnum = fileConfig.getFileActionBizEnum();
        Long userId = fileConfig.getUserId();

        // 创建完整的文件路径：<root>/<业务名称>/<用户id>
        Path directoryPath = localFileConfig.getRootLocation().resolve(filePath);

        // 检查并创建目录
        File directory = directoryPath.toFile();
        if (!directory.exists() && !directory.mkdirs())
        {
            log.error("create directory error, directory = {}", directory.getPath());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }
        // 确保文件保存到：<directoryPath>/<filename>
        File file = new File(directory, filename);
        try
        {
            multipartFile.transferTo(file); // 保存文件到指定位置
            // 返回文件的相对路径：<saveLocation>/<fileActionBizEnum>/<userId>/<filename>
            return localFileConfig.getRootLocation().resolve(
                    Paths.get(fileActionBizEnum.getLabel(),
                            String.valueOf(userId),
                            filename));
        }
        catch (Exception e)
        {
            log.error("file upload error, filepath = {}", file.getPath(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }
    }


    @Override
    public Path saveFile(UploadFileDTO uploadFileDTO) throws IOException
    {
        MultipartFile multipartFile = uploadFileDTO.getMultipartFile();
        return saveFile(multipartFile, uploadFileDTO);
    }

    @Override
    public void deleteFile(Path key) throws IOException
    {
        Path path = localFileConfig.getRootLocation().resolve(key);
        File file = path.toFile();
        if (!file.delete())
        {
            throw new IOException("Failed to delete file: " + file.getPath());
        }
    }

    /**
     * 乐观删除
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/6/23 11:02
     */
    @Override
    public Boolean deleteFileWithTolerance(Path key)
    {
        try
        {
            deleteFile(key);
            return true;
        }
        catch (IOException e)
        {
            log.error("Failed to delete file: {}", key, e);
            return false;
        }
    }

    @Override
    public Resource getFile(Path key) throws IOException
    {
        Path finalPath = localFileConfig.getRootLocation().resolve(key);
        File file = finalPath.toFile();
        if (!file.exists()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件不存在");
        }
        return new FileSystemResource(file);
    }

    @Override
    public byte[] readSlice(Path key, long offset, int length) throws IOException {
        Path finalPath = localFileConfig.getRootLocation().resolve(key);
        File file = finalPath.toFile();
        if (!file.exists()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件不存在");
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileSize = raf.length();
            // 调整 length，防止越界
            int sliceLen = (int) Math.min(length, fileSize - offset);
            if (sliceLen <= 0) {
                return new byte[0];
            }
            byte[] buffer = new byte[sliceLen];
            raf.seek(offset);
            raf.readFully(buffer);
            return buffer;
        }
    }



    @Override
    public String buildFileURL(Long userId, String fileName, FileActionBizEnum fileActionBizEnum)
    {
        String pathPattern = localFileConfig.getStaticPath() + "/" + fileActionBizEnum.getRoutePath();
        return String.format("%s%s/%s/%s", CommonConstant.BACKEND_URL + "/api", pathPattern, userId, fileName);
    }
}
