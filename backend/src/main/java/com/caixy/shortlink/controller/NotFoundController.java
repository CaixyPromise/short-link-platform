package com.caixy.shortlink.controller;

import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 404控制器
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/24 3:15
 */
@RestController
@RequestMapping("/page")
public class NotFoundController
{
    @GetMapping("/notFound")
    public Result<String> notFound() {
        return ResultUtils.success("404-NotFound: 短链不存在或不可用");
    }
}
