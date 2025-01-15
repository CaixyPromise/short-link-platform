package com.caixy.shortlink.service;

import com.caixy.shortlink.model.entity.ApiKey;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.vo.api.ApiKeyVO;
import com.caixy.shortlink.model.vo.user.UserVO;

/**
* @author CAIXYPROMISE
* @description 针对表【t_api_key(API信息表)】的数据库操作Service
* @createDate 2025-01-14 21:47:16
*/
public interface ApiKeyService extends IService<ApiKey> {

    /**
     * 初始化用户api-key
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 23:08
     */
    Boolean initApiKeyByUser(Long userId);

    /**
     * 根据用户对象生成apikey
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 22:55
     */
    ApiKeyVO generateApiKeyByUser(Long userId);

    /**
     * 刷新用户api-key
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 22:54
     */
    ApiKeyVO refreshApiKeyByUser(UserVO userVO, String base64PublicKey) throws Exception;

    /**
     * 前端使用-获取用户apikey
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 22:54
     */
    ApiKeyVO getUserApiKeyInFronted(UserVO userVO, String base64PublicKey) throws Exception;

    /**
     * 在系统内部使用-获取用户apikey
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 22:53
     */
    ApiKeyVO getUserApiKeyInSystem(UserVO userVO);
}
