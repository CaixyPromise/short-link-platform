package com.caixy.shortlink.controller;

import com.caixy.shortlink.service.LinkService;
import com.caixy.shortlink.utils.ServletUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 链接重定向接口控制器
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/27 22:23
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/s")
public class LinkRedirectController
{
    private final LinkService linkService;

    @GetMapping("/{shortUri}")
    public void redirectUrl(@PathVariable String shortUri, ServletRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response)
    {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        String serverPort = Optional.of(request.getServerPort())
                                    .filter(port -> !((port == 80 && "http".equalsIgnoreCase(scheme)) ||
                                                      (port == 443 && "https".equalsIgnoreCase(scheme))))
                                    .map(String::valueOf)
                                    .map(port -> ":" + port)
                                    .orElse("");
        String fullShortUrl = String.format("%s://%s%s/%s", scheme, serverName, serverPort, shortUri);
        String redirectResult = linkService.redirectShortLink(fullShortUrl, shortUri, httpServletRequest, response);
        ServletUtils.sendRedirect(response, redirectResult);
    }
}
