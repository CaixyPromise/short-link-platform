package com.caixy.shortlink.service.impl;

import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.exception.FileUploadActionException;
import com.caixy.shortlink.manager.UploadManager.annotation.FileUploadActionTarget;
import com.caixy.shortlink.manager.UploadManager.annotation.UploadMethodTarget;
import com.caixy.shortlink.manager.file.FileInfoHelper;
import com.caixy.shortlink.manager.redis.RedisManager;
import com.caixy.shortlink.model.dto.file.FileUploadAfterActionResult;
import com.caixy.shortlink.model.dto.file.FileUploadBeforeActionResult;
import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileRequest;
import com.caixy.shortlink.model.entity.FileInfo;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.model.enums.RedisKeyEnum;
import com.caixy.shortlink.model.enums.SaveFileMethodEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.FileInfoService;
import com.caixy.shortlink.service.FileReferenceService;
import com.caixy.shortlink.service.UploadFileService;
import com.caixy.shortlink.manager.file.strategy.FileActionStrategy;
import com.caixy.shortlink.manager.file.strategy.UploadFileMethodStrategy;
import com.caixy.shortlink.utils.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件服务实现类
 *
 * @author CAIXYPROMISE
 * @name com.caixy.shortlink.service.impl.UploadFileServiceImpl
 * @since 2024-05-21 21:55
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class UploadFileServiceImpl implements UploadFileService
{
    private final List<UploadFileMethodStrategy> uploadFileMethodStrategies;
    private final FileInfoService fileInfoService;
    private final FileReferenceService fileReferenceService;
    private final RedisManager redisManager;
    private final FileInfoHelper fileInfoHelper;

    private Map<SaveFileMethodEnum, UploadFileMethodStrategy> uploadFileMethodMap;

    private final List<FileActionStrategy> fileActionStrategy;

    private ConcurrentHashMap<FileActionBizEnum, FileActionStrategy> serviceCache;

    @PostConstruct
    public void initActionService()
    {
        serviceCache = SpringContextUtils.getServiceFromAnnotation(fileActionStrategy, FileUploadActionTarget.class, "value");
        uploadFileMethodMap = SpringContextUtils.getServiceFromAnnotation(uploadFileMethodStrategies, UploadMethodTarget.class, "value");
    }

    @Override
    public String generateUploadFileToken(String sha256, Long fileSize, UserVO userInfo, FileInfo fileInfo)
    {
        String token = UUID.randomUUID().toString();
        Map<String, Object> payload = new HashMap<>();
        payload.put("sha256", sha256);
        payload.put("fileSize", fileSize);
        payload.put("fileInfo", fileInfo);
        if (userInfo != null)
        {
            payload.put("userInfo", userInfo);
        }
        redisManager.setHashMap(RedisKeyEnum.UPLOAD_EXIST_TOKEN, payload, token);
        return token;
    }

    /**
     * 解析token数组信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/22 22:24
     */
    @Override
    public Map<String, Object> parasTokenInfoMap(String token)
    {
        return redisManager.getHashMap(RedisKeyEnum.UPLOAD_EXIST_TOKEN, token);
    }


    @Override
    public Resource getFile(FileActionBizEnum fileActionBizEnum, Path filePath) throws IOException
    {
        UploadFileMethodStrategy uploadFileMethodStrategy = safetyGetUploadFileMethod(fileActionBizEnum.getSaveFileMethod());
        return uploadFileMethodStrategy.getFile(filePath);
    }

    @Override
    public void deleteFile(FileActionBizEnum fileActionBizEnum, Path filePath)
    {
        UploadFileMethodStrategy uploadFileMethodStrategy = safetyGetUploadFileMethod(fileActionBizEnum.getSaveFileMethod());
        try
        {
            uploadFileMethodStrategy.deleteFile(filePath);
        }
        catch (IOException e)
        {
            log.error("file delete error, filepath = {}", filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除文件失败: " + e);
        }
    }


    @Override
    public void deleteFile(FileActionBizEnum fileActionBizEnum, Long userId, String filename)
    {
        Path filePath = fileActionBizEnum.buildFileAbsolutePathAndName(userId, filename);
        UploadFileMethodStrategy uploadFileMethodStrategy = safetyGetUploadFileMethod(fileActionBizEnum.getSaveFileMethod());
        try
        {
            uploadFileMethodStrategy.deleteFile(filePath);
        }
        catch (IOException e)
        {
            log.error("file delete error, filepath = {}", filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除文件失败: " + e);
        }
    }

    @Override
    public Path saveFile(UploadFileDTO uploadFileDTO) throws IOException
    {
        FileActionBizEnum fileActionBizEnum = uploadFileDTO.getFileActionBizEnum();
        UploadFileMethodStrategy uploadFileMethodStrategy = safetyGetUploadFileMethod(fileActionBizEnum.getSaveFileMethod());
        // 把上传服务处理类暴露给 后处理操作 ，例如需要删除前置文件信息等
        uploadFileDTO.setFileInfoHelper(fileInfoHelper);
        uploadFileDTO.getFileSaveInfo().setFileURL(uploadFileMethodStrategy.buildFileURL(uploadFileDTO.getUserId(), uploadFileDTO.getFileSaveInfo().getFileInnerName(), fileActionBizEnum));
        Path savePath = uploadFileMethodStrategy.saveFile(uploadFileDTO);
        if (savePath == null)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
        // 保存文件到文件表里
        FileInfo fileInfo = FileInfo.builder().fileSha256(uploadFileDTO.getSha256()).fileSize(uploadFileDTO.getFileSize()).fileInnerName(uploadFileDTO.getFileSaveInfo().getFileInnerName()).contentType(uploadFileDTO.getFileSaveInfo().getContentType()).fileInnerName(uploadFileDTO.getFileSaveInfo().getFileInnerName()).storagePath(savePath.toString()).storageType(uploadFileDTO.getFileActionBizEnum().getSaveFileMethod().getCode()).build();
        uploadFileDTO.setFileInfo(fileInfo);
        boolean fileSaved = fileInfoService.save(fileInfo);
        if (!fileSaved)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
        uploadFileDTO.setIsSaved(true);
        return savePath;
    }


    /**
     * 获取业务文件上传处理器
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/6/11 下午8:00
     */
    @Override
    public FileActionStrategy getFileActionService(FileActionBizEnum fileActionBizEnum)
    {
        FileActionStrategy actionService = serviceCache.get(fileActionBizEnum);
        if (actionService == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂无该文件对应业务的操作");
        }
        return actionService;
    }

    /**
     * 处理上传逻辑
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/10/19 上午1:27
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleUpload(UploadFileRequest uploadFileRequest, FileActionBizEnum uploadBizEnum, SaveFileMethodEnum saveFileMethod, UploadFileDTO uploadFileDTO, HttpServletRequest request)
    {
        Path savePath = null;
        try
        {
            // 获取文件处理类，如果找不到就会直接报错
            FileActionStrategy actionService = getFileActionService(uploadBizEnum);
            FileUploadBeforeActionResult doVerifyFileToken = doBeforeFileUploadAction(actionService, uploadFileDTO, uploadFileRequest);
            if (!doVerifyFileToken.getSuccess())
            {
                log.error("{}-验证token：文件上传失败，文件信息：{}, 上传用户Id: {}", saveFileMethod.getDesc(), uploadFileDTO.getFileSaveInfo(), uploadFileDTO.getUserId());
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
            }
            if (uploadFileDTO.getIsSaved() == null || uploadFileDTO.getIsSaved().equals(Boolean.FALSE))
            {
                // 不是秒存，需要保存文件
                savePath = saveFile(uploadFileDTO);
                log.info("{}：文件上传成功，文件路径：{}", saveFileMethod.getDesc(), savePath);
            }
            FileUploadAfterActionResult doAfterFileUpload = doFileUploadAfterAction(actionService, uploadFileDTO, savePath, uploadFileRequest, request);
            if (!doAfterFileUpload.getSuccess())
            {
                log.error("{}：文件上传成功，文件路径：{}，但后续处理失败", saveFileMethod.getDesc(), savePath);
                deleteFile(uploadFileDTO.getFileActionBizEnum(), savePath);

                log.error("{}：文件上传成功，文件路径：{}，后处理失败后，成功删除文件", saveFileMethod.getDesc(), savePath);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传成功，但后续处理失败");
            }
            // 添加文件关联信息表
            fileReferenceService.bindFileReference(
                    uploadFileDTO.getFileInfo().getId(),
                    uploadFileDTO.getUserId(),
                    uploadFileDTO.getFileActionBizEnum(),
                    doAfterFileUpload);
            return uploadFileDTO.getFileSaveInfo().getFileURL();
        }
        catch (FileUploadActionException | BusinessException | IOException e)
        {
            log.error("{}: 文件上传失败，错误信息: {}", saveFileMethod.getDesc(), e.getMessage());
            // 如果 savePath 不为空，则意味着文件已经上传成功，需要删除它
            if (savePath != null)
            {
                deleteFile(uploadBizEnum, savePath);
                log.info("{}：文件上传失败，删除文件成功，文件路径：{}", saveFileMethod.getDesc(), savePath);
            }
            // 抛出业务异常，以触发事务回滚
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    private FileUploadAfterActionResult doFileUploadAfterAction(FileActionStrategy actionService, UploadFileDTO uploadFileDTO, Path savePath, UploadFileRequest uploadFileRequest, HttpServletRequest request) throws IOException
    {
        return actionService.doAfterUploadAction(uploadFileDTO, savePath, uploadFileRequest, request);
    }

    private FileUploadBeforeActionResult doBeforeFileUploadAction(FileActionStrategy actionService, UploadFileDTO uploadFileDTO, UploadFileRequest uploadFileRequest)
    {
        return actionService.doBeforeUploadAction(uploadFileDTO, uploadFileRequest);
    }

    private UploadFileMethodStrategy safetyGetUploadFileMethod(SaveFileMethodEnum saveFileMethodEnum)
    {
        UploadFileMethodStrategy uploadFileMethodStrategy = uploadFileMethodMap.get(saveFileMethodEnum);
        if (uploadFileMethodStrategy == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文件操作方式");
        }
        return uploadFileMethodStrategy;
    }
}
