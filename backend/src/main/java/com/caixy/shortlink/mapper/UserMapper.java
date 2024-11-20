package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据库操作
 */
public interface UserMapper extends BaseMapper<User>
{
    int userDeletion(@Param("userId") Long userId);

    User findByNickname(@Param("nickName") String nickName);
}




