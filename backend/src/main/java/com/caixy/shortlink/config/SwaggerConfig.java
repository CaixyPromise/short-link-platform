package com.caixy.shortlink.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Swagger配置类，解决和Actuator错误
 *
 * @Author CAIXYPROMISE
 * @name com.caixy.shortlink.config.SwaggerConfig
 * @since 2024/10/31 22:11
 */
//@Configuration
public class SwaggerConfig {

//    @Bean
    public WebMvcEndpointHandlerMapping webMvcEndpointHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
                                                                     EndpointMediaTypes endpointMediaTypes,
                                                                     CorsEndpointProperties corsEndpointProperties,
                                                                     WebEndpointProperties webEndpointProperties,
                                                                     Environment environment) {
        // Collect all endpoints from the WebEndpointsSupplier
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);

        // Base path and endpoint mappings
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);

        // Check if we should register links mapping
        boolean shouldRegisterLinksMapping = shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);

        // Return the WebMvcEndpointHandlerMapping with the collected endpoints
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
                corsEndpointProperties.toCorsConfiguration(), new EndpointLinksResolver(
                allEndpoints, basePath), shouldRegisterLinksMapping);
    }

    /**
     * Determines if the links mapping should be registered
     *
     * @param webEndpointProperties
     * @param environment
     * @param basePath
     * @return
     */
    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment,
                                               String basePath) {
        // If discovery is enabled or base path is set or the management port is different, return true
        if (webEndpointProperties.getDiscovery().isEnabled()) {
            return StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT);
        }
        // Default to false if discovery is not enabled
        return false;
    }
}
