package com.caixy.shortlink.model.vo.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取注册信息视图，用在点击链接进入完成注册页面
 *
 * @Author CAIXYPROMISE
 * @since 2025/2/11 17:40
 */
@Data
@Builder
public class RegistrationInfo implements Serializable
{
    private String email;

    private String nickName;

    private static final long serialVersionUID = 1L;
}
