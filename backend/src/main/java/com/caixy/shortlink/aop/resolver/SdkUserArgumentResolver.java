package com.caixy.shortlink.aop.resolver;

import com.caixy.shortlink.annotation.SdkUser;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.model.vo.user.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * SDK参数解析器-判断当前方法参数需要注入 UserVO
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/15 2:36
 */
@Component
public class SdkUserArgumentResolver implements HandlerMethodArgumentResolver
{

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserVO.class)
               && parameter.hasParameterAnnotation(SdkUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception
    {
        HttpServletRequest request = ((ServletWebRequest)webRequest).getRequest();
        return request.getAttribute(CommonConstant.SDK_USER_KEY); // 拦截器里设置的 key
    }
}
