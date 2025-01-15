package com.caixy.shortlink.controller;

import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.manager.Authorization.AuthManager;
import com.caixy.shortlink.model.dto.api.QueryApiKeyRequest;
import com.caixy.shortlink.model.vo.api.ApiKeyVO;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API信息接口控制器
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/14 21:50
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api-key")
public class ApiInfoController
{
    private final ApiKeyService apiKeyService;
    private final AuthManager authManager;

    /**
     * 刷新api-key
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 23:02
     */
    @PostMapping("/refresh")
    public Result<ApiKeyVO> refreshApiKey(@RequestBody @Validated QueryApiKeyRequest queryApiKeyRequest)
    {
        UserVO loginUser = authManager.getLoginUser();
        try
        {
            ApiKeyVO apiKeyVO = apiKeyService.refreshApiKeyByUser(loginUser, queryApiKeyRequest.getPublicKey());
            return ResultUtils.success(apiKeyVO);
        }
        catch (Exception e)
        {
            log.error("刷新API密钥失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "刷新API密钥失败, 请稍后再试:(");
        }
    }

    @PostMapping("/query")
    public Result<ApiKeyVO> queryApiKey(@RequestBody @Validated QueryApiKeyRequest queryApiKeyRequest) {
        UserVO loginUser = authManager.getLoginUser();
        try {
            ApiKeyVO apiKeyVO = apiKeyService.getUserApiKeyInFronted(loginUser, queryApiKeyRequest.getPublicKey());
            return ResultUtils.success(apiKeyVO);
        }
        catch (Exception e) {
            log.error("查询API密钥失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询API密钥失败, 请稍后再试:(");
        }
    }
}
