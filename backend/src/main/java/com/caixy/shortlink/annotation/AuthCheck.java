package com.caixy.shortlink.annotation;

import com.caixy.shortlink.model.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck
{

    /**
     * 必须有某个角色
     *
     * @return
     */
    UserRoleEnum mustRole();

}

