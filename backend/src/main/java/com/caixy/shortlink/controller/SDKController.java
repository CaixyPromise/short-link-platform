package com.caixy.shortlink.controller;

import com.caixy.shortlink.annotation.SdkUser;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.model.dto.link.LinkAddRequest;
import com.caixy.shortlink.model.enums.ShortLinkCreateType;
import com.caixy.shortlink.model.vo.link.LinkCreateVO;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.LinkService;
import com.caixy.shortlink.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * SDK调用接口控制器
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/15 1:30
 */
@Slf4j
@RestController
@RequestMapping("/sdk")
@RequiredArgsConstructor
public class SDKController
{
    private final LinkService linkService;
    private final UserService userService;
    @PostMapping("/test")
    public Result<LinkCreateVO> sdkTest(@RequestBody @Validated LinkAddRequest addLinkSdkRequest, @SdkUser UserVO userVO) {
        log.info("SDK调用接口，参数：{}", addLinkSdkRequest);
        return ResultUtils.success(linkService.addShortLink(addLinkSdkRequest, ShortLinkCreateType.SDK));
    }
}
