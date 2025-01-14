package com.caixy.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.shortlink.common.Result;
import com.caixy.shortlink.common.ResultUtils;
import com.caixy.shortlink.model.dto.feedback.PageMyFeedbackRequest;
import com.caixy.shortlink.model.dto.feedback.PostFeedbackRequest;
import com.caixy.shortlink.model.vo.feedback.UserFeedbackInfoVO;
import com.caixy.shortlink.service.FeedbackInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈信息接口控制器
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/14 15:31
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController
{
    private final FeedbackInfoService feedbackService;
    /**
     * 发送评论
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 15:52
     */
    @PostMapping("/post")
    public Result<Long> postFeedback(@RequestBody @Validated PostFeedbackRequest postFeedbackRequest)
    {
        return ResultUtils.success(feedbackService.postFeedbackInfo(postFeedbackRequest));
    }

    /**
     * 用户获取“我的反馈信息”
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 16:13
     */
    @PostMapping("/page/my")
    public Result<IPage<UserFeedbackInfoVO>> getMyFeedbackPage(@RequestBody PageMyFeedbackRequest pageMyFeedbackRequest) {
        return ResultUtils.success(feedbackService.pageUserFeedbackInfo(pageMyFeedbackRequest));
    }
}
