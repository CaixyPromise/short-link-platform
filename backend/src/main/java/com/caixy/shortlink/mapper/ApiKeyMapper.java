package com.caixy.shortlink.mapper;

import com.caixy.shortlink.model.entity.ApiKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.apache.ibatis.annotations.Param;

/**
* @author CAIXYPROMISE
* @description 针对表【t_api_key(API信息表)】的数据库操作Mapper
* @createDate 2025-01-14 21:47:16
* @Entity com.caixy.shortlink.model.entity.ApiKey
*/
public interface ApiKeyMapper extends BaseMapper<ApiKey> {
    UserVO selectUserVOByAccessKey(@Param("accessKey") String accessKey);
    ApiKey selectSecretKeyByAccessKey(@Param("accessKey") String accessKey);
}




