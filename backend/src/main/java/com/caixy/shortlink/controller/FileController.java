package com.caixy.shortlink.controller;

import cn.hutool.core.io.FileUtil;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.authorization.AuthManager;
import com.caixy.shortlink.manager.UploadManager.utils.FileUtils;
import com.caixy.shortlink.model.dto.file.CheckFileExistResponse;
import com.caixy.shortlink.model.dto.file.DownloadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileDTO;
import com.caixy.shortlink.model.dto.file.UploadFileRequest;
import com.caixy.shortlink.model.entity.FileInfo;
import com.caixy.shortlink.model.enums.FileActionBizEnum;
import com.caixy.shortlink.model.enums.SaveFileMethodEnum;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.FileInfoService;
import com.caixy.shortlink.service.UploadFileService;
import com.caixy.shortlink.manager.file.strategy.FileActionStrategy;
import com.caixy.shortlink.utils.MapUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController
{
    @Resource
    private AuthManager authManager;

    @Resource
    private UploadFileService uploadFileService;

    @Resource
    private FileInfoService fileInfoService;

    /**
     * 秒传接口：检查文件是否存在
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/4/22 19:32
     */
    @GetMapping("/check_exist")
    public Result<CheckFileExistResponse> checkFileExist(
            @RequestParam("scene") Integer scene, // 上传业务场景
            @RequestParam("sha256") String sha256, // sha256
            @RequestParam("size") Long fileSize) // 文件大小（字节）
    {
        FileActionBizEnum fileActionBizEnum = FileActionBizEnum.getEnumByValue(scene);
        if (fileActionBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传业务场景不存在");
        }
        UserVO loginUser = authManager.getLoginUser();
        FileInfo fileInfo = fileInfoService.findFileBySha256AndSize(sha256, fileSize);
        CheckFileExistResponse fileExistResponse = CheckFileExistResponse.builder()
                .exist(fileInfo != null)
                // 无论是否存在，都会返回token，后续上传和关联文件只认可token，不认可用户上传的值。
                .token(uploadFileService.generateUploadFileToken(sha256, fileSize, loginUser, fileInfo))
                .build();
        return ResultUtils.success(fileExistResponse);
    }


    /**
     * 处理上传文件，上传成功则直接返回文件访问路径
     *
     * @author CAIXYPROMISE
     * @version 2.0 fix 事务失效问题，更加符合规范
     * @since 2024/10/19 上午1:33
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestPart("file") MultipartFile multipartFile, UploadFileRequest uploadFileRequest, HttpServletRequest request)
    {
        Map<String, Object> fileInfoMap = uploadFileService.parasTokenInfoMap(uploadFileRequest.getToken());
        if (fileInfoMap == null || fileInfoMap.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传token无效");
        }
        String sha256 = MapUtils.safetyGetValueByKey(fileInfoMap, "sha256", String.class);
        Long fileSize = MapUtils.safetyGetValueByKey(fileInfoMap, "fileSize", Long.class);
        FileInfo fileInfo = MapUtils.safetyGetValueByKey(fileInfoMap, "fileInfo", FileInfo.class);
        try {
            UploadFileDTO uploadFileDTO = getUploadFileConfig(multipartFile, uploadFileRequest, request);
            if (fileInfo != null) {
                uploadFileDTO.setFileInfo(fileInfo);
                uploadFileDTO.setIsSaved(true);
                if (!sha256.equals(uploadFileDTO.getSha256()) && !fileSize.equals(uploadFileDTO.getFileSize())) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件信息不匹配");
                }
            }
            FileActionBizEnum uploadBizEnum = uploadFileDTO.getFileActionBizEnum();
            SaveFileMethodEnum saveFileMethod = uploadFileDTO.getFileActionBizEnum().getSaveFileMethod();
            return ResultUtils.success(uploadFileService.handleUpload(uploadFileRequest, uploadBizEnum, saveFileMethod, uploadFileDTO, request));
        } catch (IOException E) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败");
        }
    }

    @GetMapping("/download")
    public void downloadFileById(@RequestParam("id") String id, @RequestParam("bizName") Integer bizName, HttpServletRequest request, HttpServletResponse response)
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
                org.springframework.core.io.Resource fileResource = uploadFileService.getFile(fileActionBizEnum, fileKey);
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

    /**
     * 校验文件
     *
     * @param multipartFile
     */
    private FileActionBizEnum validFile(MultipartFile multipartFile, UploadFileRequest uploadFileRequest)
    {
        Integer biz = uploadFileRequest.getBiz();
        FileActionBizEnum fileActionBizEnum = FileActionBizEnum.getEnumByValue(biz);
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
        boolean lessThanOrEqualTo = fileActionBizEnum.getMaxSize().isLessThanOrEqualTo(fileSize);
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
    private UploadFileDTO getUploadFileConfig(MultipartFile multipartFile, UploadFileRequest uploadFileRequest, HttpServletRequest request) throws IOException
    {
        FileActionBizEnum fileActionBizEnum = validFile(multipartFile, uploadFileRequest);
        UserVO loginUser = authManager.getLoginUser();
        UploadFileDTO uploadFileDTO = new UploadFileDTO();
        uploadFileDTO.setFileActionBizEnum(fileActionBizEnum);
        uploadFileDTO.setMultipartFile(multipartFile);
        uploadFileDTO.setUserId(loginUser.getId());
        uploadFileDTO.setSha256(FileUtils.getMultiPartFileSha256(multipartFile));
        uploadFileDTO.setFileSize(multipartFile.getSize());
        UploadFileDTO.FileSaveInfo fileSaveInfo = uploadFileDTO.convertFileInfo();
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


}
