package com.caixy.shortlink.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.common.ErrorCode;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.exception.BusinessException;
import com.caixy.shortlink.model.entity.ApiKey;
import com.caixy.shortlink.model.vo.api.ApiKeyVO;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.ApiKeyService;
import com.caixy.shortlink.mapper.ApiKeyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @author CAIXYPROMISE
 * @description 针对表【t_api_key(API信息表)】的数据库操作Service实现
 * @createDate 2025-01-14 21:47:16
 */
@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKey> implements ApiKeyService
{
    private final ApiKeyMapper apiKeyMapper;


    @Override
    public Boolean initApiKeyByUser(Long userId)
    {
        ApiKeyVO apiKeyVO = generateApiKeyByUser(userId);
        ApiKey apiKey = new ApiKey();
        apiKey.setUserId(userId);
        apiKey.setAccessKey(apiKeyVO.getAccessKey());
        apiKey.setSecretKey(apiKeyVO.getSecretKey());
        apiKey.setIsDeleted(CommonConstant.NOT_DELETE_FLAG);
        return apiKeyMapper.insert(apiKey) > 0;
    }

    @Override
    public ApiKeyVO generateApiKeyByUser(Long userId)
    {
        if (userId == null)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成Api-key失败: 用户id不能为空");
        }
        String apiKeyPayload = userId + RandomUtil.randomNumbers(6) + System.currentTimeMillis();
        String secretKeyPayload = userId + RandomUtil.randomString(6) + System.currentTimeMillis() + UUID.randomUUID();
        return ApiKeyVO.builder()
                       .accessKey(DigestUtil.md5Hex(apiKeyPayload))
                       .secretKey(DigestUtil.md5Hex(secretKeyPayload))
                       .build();
    }

    @Override
    public ApiKeyVO refreshApiKeyByUser(UserVO userVO, String base64PublicKey) throws Exception
    {
        ApiKey userApiKey = fetchOrInitApiKey(userVO);
        ApiKeyVO newKeys = generateApiKeyByUser(userVO.getId());

        userApiKey.setAccessKey(newKeys.getAccessKey());
        userApiKey.setSecretKey(newKeys.getSecretKey());
        userApiKey.setIsDeleted(CommonConstant.NOT_DELETE_FLAG);
        userApiKey.setLastRefreshTime(new Date());

        if (userApiKey.getId() == null)
        {
            apiKeyMapper.insert(userApiKey);
        }
        else
        {
            apiKeyMapper.updateById(userApiKey);
        }
        return encryptApiKeyVO(newKeys, base64PublicKey);
    }

    @Override
    public ApiKeyVO getUserApiKeyInFronted(UserVO userVO, String base64PublicKey) throws Exception
    {
        ApiKeyVO apiKeyVO = getUserApiKeyInSystem(userVO);
        return encryptApiKeyVO(apiKeyVO, base64PublicKey);
    }

    @Override
    public ApiKeyVO getUserApiKeyInSystem(UserVO userVO)
    {
        LambdaQueryWrapper<ApiKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiKey::getUserId, userVO.getId());
        ApiKey userApiKey = apiKeyMapper.selectOne(queryWrapper);
        if (userApiKey == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户 API Key 不存在");
        }
        return ApiKeyVO.builder()
                       .accessKey(userApiKey.getAccessKey())
                       .secretKey(userApiKey.getSecretKey())
                       .build();
    }

    /**
     * 获取用户API Key，如果不存在则初始化
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 22:53
     */
    private ApiKey fetchOrInitApiKey(UserVO userVO)
    {
        LambdaQueryWrapper<ApiKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiKey::getUserId, userVO.getId());
        ApiKey userApiKey = apiKeyMapper.selectOne(queryWrapper);
        if (userApiKey == null)
        {
            userApiKey = new ApiKey();
            userApiKey.setUserId(userVO.getId());
        }
        return userApiKey;
    }

    /**
     * 加密API Key 用于网络传输
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 22:53
     */
    private ApiKeyVO encryptApiKeyVO(ApiKeyVO apiKeyVO, String base64PublicKey) throws Exception
    {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

        // 显式指定主哈希 = SHA-256，MGF1 = SHA-256
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",                // main hash
                "MGF1",                   // mask generation function
                MGF1ParameterSpec.SHA256, // mgf1 also uses SHA-256
                PSource.PSpecified.DEFAULT
        );

        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);

        String encryptedSecretKey = Base64.getEncoder().encodeToString(
                cipher.doFinal(apiKeyVO.getSecretKey().getBytes())
        );
        String encryptedApiKey = Base64.getEncoder().encodeToString(
                cipher.doFinal(apiKeyVO.getAccessKey().getBytes())
        );
        apiKeyVO.setSecretKey(encryptedSecretKey);
        apiKeyVO.setAccessKey(encryptedApiKey);
        return apiKeyVO;
    }

}




