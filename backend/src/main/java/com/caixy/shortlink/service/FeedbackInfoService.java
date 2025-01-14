package com.caixy.shortlink.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caixy.shortlink.model.dto.feedback.PageMyFeedbackRequest;
import com.caixy.shortlink.model.dto.feedback.PostFeedbackRequest;
import com.caixy.shortlink.model.entity.FeedbackInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.shortlink.model.vo.feedback.UserFeedbackInfoVO;

/**
* @author CAIXYPROMISE
* @description 针对表【t_feedback_info(反馈信息表)】的数据库操作Service
* @createDate 2025-01-14 15:37:44
*/
public interface FeedbackInfoService extends IService<FeedbackInfo> {
    /**
     * 提交反馈信息，返回feedbackId
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 15:50
     */
    Long postFeedbackInfo(PostFeedbackRequest postFeedbackRequest);

    IPage<UserFeedbackInfoVO> pageUserFeedbackInfo(PageMyFeedbackRequest pageMyFeedbackRequest);
}
