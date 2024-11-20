package com.caixy.shortlink;

import com.caixy.shortlink.utils.NetUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 主类（项目启动入口）
 */
@SpringBootApplication()
@MapperScan("com.caixy.shortlink.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication
{
    public static void main(String[] args)
    {
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String serverHost = NetUtils.getHostIp();
        System.setProperty("app.startup-time", startTime);
        System.setProperty("app.serverHost", serverHost);
        SpringApplication.run(MainApplication.class, args);
    }
}
