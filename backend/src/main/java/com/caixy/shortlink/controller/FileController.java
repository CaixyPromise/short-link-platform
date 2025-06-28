package com.caixy.shortlink.controller;

import cn.hutool.core.io.FileUtil;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.authorization.AuthManager;
import com.caixy.shortlink.manager.file.domain.FileHmacInfo;
import com.caixy.shortlink.utils.FileUtils;
import com.caixy.shortlink.manager.file.domain.UploadContext;
import com.caixy.shortlink.model.dto.file.CheckFileExistResponse;
import com.caixy.shortlink.model.dto.file.DownloadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileRequest;
import com.caixy.shortlink.model.entity.FileInfo;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.FileInfoService;
import com.caixy.shortlink.service.FileReferenceService;
import com.caixy.shortlink.service.UploadFileService;
import com.caixy.shortlink.manager.file.strategy.FileActionStrategy;
import com.caixy.shortlink.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileController
{
    private final AuthManager authManager;

    private final UploadFileService uploadFileService;

    private final FileInfoService fileInfoService;
    /**
     * 秒传接口：检查文件是否存在
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/22 19:32
     */
    @GetMapping("/check")
    public Result<CheckFileExistResponse> checkFileExist(@RequestParam("scene") Integer scene, // 上传业务场景
                                                         @RequestParam("sha256") String sha256, // sha256
                                                         @RequestParam("size") Long fileSize) // 文件大小（字节）
    {
        FileActionBizEnum fileActionBizEnum = FileActionBizEnum.getEnumByValue(scene);
        if (fileActionBizEnum == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传业务场景不存在");
        }
        UserVO loginUser = authManager.getLoginUser();
        FileInfo fileInfo = fileInfoService.findFileBySha256AndSize(sha256, fileSize);
        CheckFileExistResponse.CheckFileExistResponseBuilder responseBuilder = CheckFileExistResponse
                .builder()
                 // 无论是否存在，都会返回token，后续上传和关联文件只认可token，不认可用户上传的值。
                 .token(UUID.randomUUID().toString());
        HashMap<String, Object> cacheMap = new HashMap<>();
        if (fileInfo != null) {
            // 如果文件存在，则生成HMAC challenge
            FileHmacInfo hmacEncryptInfo = FileHmacInfo.build(fileInfo.getFileSize());
            // calcServerFileHmac(fileInfo, hmacEncryptInfo.getSalt());
            responseBuilder.challenge(hmacEncryptInfo);
            cacheMap.put("hmacBody", hmacEncryptInfo);
        }
        cacheMap.put("sha256", sha256);
        cacheMap.put("fileSize", fileSize);
        cacheMap.put("userInfo", loginUser);
        cacheMap.put("fileInfo", fileInfo);

        CheckFileExistResponse fileExistResponse = responseBuilder.build();
        uploadFileService.setCheckFileCache(cacheMap, fileExistResponse.getToken());
        return ResultUtils.success(fileExistResponse);
    }

    /**
     * 秒传接口：上传文件
     *
     * @author CAIXYPROMISE
     * @version 2.0 添加防重放验证和token清理
     * @version 2025/1/27 16:43
     */
    @PostMapping("/upload/faster")
    public Result<String> uploadFileFaster(@RequestBody @Valid UploadFileRequest uploadFileRequest,
                                           HttpServletRequest request)
    {
        UserVO loginUser = authManager.getLoginUser();
        Map<String, Object> fileInfoMap = validUploadTokenAndGetInfoMap(
                uploadFileRequest.getToken(), 
                uploadFileRequest.getNonce(),
                uploadFileRequest.getTimestamp(),
                loginUser.getId()
        );
        Object hmacBody = fileInfoMap.get("hmacBody");
        // 如果token对应的缓存信息没有hmac信息，则说明不符合秒传的要求。
        if (hmacBody == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "token不合法");
        }
        
        try {
            String result = uploadFileService.handleFasterUpload(
                    UploadContext.fromRequest(uploadFileRequest,
                                          request,
                                          loginUser.getId(),
                                          fileInfoMap,
                                          null)
            );
            // 请求成功后清理token缓存
            uploadFileService.clearTokenCache(uploadFileRequest.getToken());
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            // 请求失败时不清理token缓存，允许降级到普通上传
            uploadFileService.degradeToNormalUpload(fileInfoMap, uploadFileRequest.getToken());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    /**
     * 处理上传文件，上传成功则直接返回文件访问路径
     *
     * @return 返回可访问路径
     * @author CAIXYPROMISE
     * @version 3.0 添加防重放验证和token清理
     * @since 2024/10/19 上午1:33
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "上传文件")
    public Result<String> uploadFile(
            @Parameter(description = "上传文件") @RequestPart("file") MultipartFile multipartFile,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UploadFileRequest.class)
                    )
            )
            @RequestPart("uploadFileRequest") UploadFileRequest uploadFileRequest,
            HttpServletRequest request
    )
    {
        UserVO loginUser = authManager.getLoginUser();
        Map<String, Object> fileInfoMap = validUploadTokenAndGetInfoMap(
                uploadFileRequest.getToken(), 
                uploadFileRequest.getNonce(),
                uploadFileRequest.getTimestamp(),
                loginUser.getId()
        );
        Object hmacBody = fileInfoMap.get("hmacBody");
        Object isDegrade = fileInfoMap.get("degrade");
        // 如果token对应的缓存信息有hmac信息，则说明不符合普传的要求。
        if (hmacBody != null && isDegrade != null && !Boolean.parseBoolean(isDegrade.toString()))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "token不合法");
        }
        try
        {
            UploadFileDTO uploadFileDTO = getUploadFileConfig(multipartFile, uploadFileRequest, request);
            UploadContext uploadContext = UploadContext
                    .fromRequest(uploadFileRequest,
                                 request,
                                 loginUser.getId(),
                                 fileInfoMap,
                                 uploadFileDTO);
            String result = uploadFileService.handleUpload(uploadContext);
            // 请求成功后清理token缓存
            uploadFileService.clearTokenCache(uploadFileRequest.getToken());
            return ResultUtils.success(result);
        }
        catch (IOException E)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败");
        }
    }

    @GetMapping("/download")
    public void downloadFileById(@RequestParam("id") String id, @RequestParam("bizName") Integer bizName,
                                 HttpServletRequest request, HttpServletResponse response)
    {
        FileActionBizEnum fileActionBizEnum = FileActionBizEnum.getEnumByValue(bizName);
        if (fileActionBizEnum == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "业务类型不存在");
        }
        UserVO loginUser = authManager.getLoginUser();

        FileActionStrategy fileActionStrategy = uploadFileService.getFileActionService(fileActionBizEnum);
        DownloadFileDTO downloadFileDTO = new DownloadFileDTO();
        downloadFileDTO.setFileId(id);
        downloadFileDTO.setFileActionBizEnum(fileActionBizEnum);
        downloadFileDTO.setUserId(loginUser.getId());
        Boolean beforeDownloadAction = fileActionStrategy.doBeforeDownloadAction(downloadFileDTO);
        if (!beforeDownloadAction)
        {
            log.error("文件下载操作前处理失败: userId: {}, 下载文件信息：{}", loginUser.getId(), downloadFileDTO);
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "文件下载操作失败");
        }
        Path fileKey = downloadFileDTO.getFilePath();
        try
        {
            if (fileKey == null)
            {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件不存在");
            }
            else
            {
                Resource fileResource = uploadFileService.getFile(fileActionBizEnum, fileKey);
                if (fileResource == null)
                {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件不存在");
                }
                else
                {
                    // 设置响应头
                    buildDownloadResponse(downloadFileDTO, response);

                    // 将文件写入响应输出流
                    StreamUtils.copy(fileResource.getInputStream(), response.getOutputStream());
                    response.flushBuffer();

                    // 执行下载后的操作
                    downloadFileDTO.setFileIsExist(true);
                    if (!fileActionStrategy.doAfterDownloadAction(downloadFileDTO))
                    {
                        log.error("文件下载操作后处理失败: userId: {}, 下载文件信息：{}", loginUser.getId(), downloadFileDTO);
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件下载操作失败");
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件不存在");
        }
    }

    private Map<String, Object> validUploadTokenAndGetInfoMap(String token, String nonce, Long timestamp, Long userId)
    {
        Map<String, Object> fileInfoMap = uploadFileService.parasTokenInfoMap(token, nonce, timestamp, userId);
        if (fileInfoMap == null || fileInfoMap.isEmpty())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传token无效");
        }
        return fileInfoMap;
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     */
    private FileActionBizEnum validFile(MultipartFile multipartFile, UploadFileRequest uploadFileRequest)
    {
        FileActionBizEnum fileActionBizEnum = uploadFileRequest.getBiz();
        if (fileActionBizEnum == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());

        Set<String> acceptFileSuffixList = fileActionBizEnum.getFileSuffix();
        if (!acceptFileSuffixList.contains(fileSuffix))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件格式不正确");
        }
        boolean lessThanOrEqualTo = fileActionBizEnum
                .getMaxSize()
                .isLessThanOrEqualTo(fileSize);
        if (!lessThanOrEqualTo)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小超过限制");
        }
        return fileActionBizEnum;
    }

    /**
     * 获取上传文件配置信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/5/21 下午10:54
     */
    private UploadFileDTO getUploadFileConfig(MultipartFile multipartFile, UploadFileRequest uploadFileRequest,
                                              HttpServletRequest request) throws IOException
    {
        FileActionBizEnum fileActionBizEnum = validFile(multipartFile, uploadFileRequest);
        UserVO loginUser = authManager.getLoginUser();
        UploadFileDTO uploadFileDTO = new UploadFileDTO();
        uploadFileDTO.setFileActionBizEnum(fileActionBizEnum);
        uploadFileDTO.setMultipartFile(multipartFile);
        uploadFileDTO.setUserId(loginUser.getId());
        uploadFileDTO.setSha256(FileUtils.getMultiPartFileSha256(multipartFile));
        uploadFileDTO.setFileSize(multipartFile.getSize());
        UploadFileDTO.FileSaveInfo fileSaveInfo = uploadFileDTO.extractFileInfo();
        uploadFileDTO.setFileSaveInfo(fileSaveInfo);
        return uploadFileDTO;
    }

    private void buildDownloadResponse(DownloadFileDTO downloadFileDTO, HttpServletResponse response)
    {
        String fileName = downloadFileDTO.getFileRealName();
        response.setContentType("application/octet-stream;charset=UTF-8;filename=" + fileName);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
    }

    private String calcServerFileHmac(FileInfo fileInfo, String salt)
    {
        Integer storageType = fileInfo.getStorageType();
        String storagePath = fileInfo.getStoragePath();
        if (storageType == null || StringUtils.isEmpty(storagePath))
        {
            return null;
        }
        FileActionBizEnum fileActionBizEnum = FileActionBizEnum.getEnumByValue(storageType);
        try
        {
            Resource resource = uploadFileService.getFile(fileActionBizEnum, Path.of(storagePath));
            return FileUtils.hmacSha256File(resource.getContentAsByteArray(), salt);

        }
        catch (IOException e)
        {
            log.error("秒传检查计算文件HMAC失败，文件不存在。文件ID: {}, 文件存储路径: {}", fileInfo.getId(), storagePath, e);
            return null;
        }
    }
}
