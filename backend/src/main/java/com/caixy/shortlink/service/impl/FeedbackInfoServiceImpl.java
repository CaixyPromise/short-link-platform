package com.caixy.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.shortlink.manager.Authorization.AuthManager;
import com.caixy.shortlink.model.convertor.feedback.FeedbackConvertor;
import com.caixy.shortlink.model.dto.feedback.PageMyFeedbackRequest;
import com.caixy.shortlink.model.dto.feedback.PostFeedbackRequest;
import com.caixy.shortlink.model.entity.FeedbackInfo;
import com.caixy.shortlink.model.vo.feedback.UserFeedbackInfoVO;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.FeedbackInfoService;
import com.caixy.shortlink.mapper.FeedbackInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author CAIXYPROMISE
 * @description 针对表【t_feedback_info(反馈信息表)】的数据库操作Service实现
 * @createDate 2025-01-14 15:37:44
 */
@Service
@RequiredArgsConstructor
public class FeedbackInfoServiceImpl extends ServiceImpl<FeedbackInfoMapper, FeedbackInfo>
        implements FeedbackInfoService
{
    private final AuthManager authManager;

    @Override
    public Long postFeedbackInfo(PostFeedbackRequest postFeedbackRequest)
    {
        UserVO loginUser = authManager.getLoginUser();
        FeedbackInfo feedbackInfo = FeedbackInfo.builder()
                                                .content(postFeedbackRequest.getContent())
                                                .contactEmail(postFeedbackRequest.getContactEmail())
                                                .contactName(postFeedbackRequest.getContactName())
                                                .title(postFeedbackRequest.getTitle())
                                                .creatorId(loginUser.getId())
                                                .build();
        return save(feedbackInfo) ? feedbackInfo.getId() : null;
    }

    /**
     * 分页查询用户反馈信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @version 2025/1/14 16:11
     */
    @Override
    public IPage<UserFeedbackInfoVO> pageUserFeedbackInfo(PageMyFeedbackRequest pageMyFeedbackRequest) {
        int current = pageMyFeedbackRequest.getCurrent();
        int size = pageMyFeedbackRequest.getPageSize();
        LambdaQueryWrapper<FeedbackInfo> queryWrapper = getQueryWrapper(pageMyFeedbackRequest);
        queryWrapper.eq(FeedbackInfo::getCreatorId, authManager.getLoginUser().getId());
        Page<FeedbackInfo> pageResult = page(new Page<>(current, size), queryWrapper);
        return pageResult.convert(FeedbackConvertor.INSTANCE::toUserFeedbackInfoVO);
    }

    private LambdaQueryWrapper<FeedbackInfo> getQueryWrapper(PageMyFeedbackRequest postFeedbackRequest) {
        LambdaQueryWrapper<FeedbackInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (postFeedbackRequest != null) {
            if (postFeedbackRequest.getTitle() != null) {
                queryWrapper.like(FeedbackInfo::getTitle, postFeedbackRequest.getTitle());
            }
            if (postFeedbackRequest.getId() != null) {
                queryWrapper.eq(FeedbackInfo::getId, postFeedbackRequest.getId());
            }
        }
        return queryWrapper;
    }
}




