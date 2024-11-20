package com.caixy.shortlink.config;

import com.caixy.shortlink.model.enums.FileActionBizEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 全局跨域配置
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer
{
    private final LocalFileConfig localFileConfig;

    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry)
    {
        for (FileActionBizEnum bizEnum : FileActionBizEnum.values())
        {
            String pathPattern = localFileConfig.getStaticPath() + "/" + bizEnum.getRoutePath() + "/**";
            String location =
                    "file:///" + localFileConfig.getRootLocation().toString().replace("\\", "/") + "/" + bizEnum.getValue() + "/";
            log.info("AddResourceHandlers: {} -> \"{}\"", pathPattern, location);
            registry.addResourceHandler(pathPattern).addResourceLocations(location);
        }
    }
}
