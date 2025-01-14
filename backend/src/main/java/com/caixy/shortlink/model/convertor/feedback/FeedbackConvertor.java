package com.caixy.shortlink.model.convertor.feedback;

import com.caixy.shortlink.model.entity.FeedbackInfo;
import com.caixy.shortlink.model.vo.feedback.UserFeedbackInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 反馈信息转换器
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/14 16:08
 */
@Mapper
public interface FeedbackConvertor
{
    FeedbackConvertor INSTANCE = Mappers.getMapper(FeedbackConvertor.class);

    UserFeedbackInfoVO toUserFeedbackInfoVO(FeedbackInfo feedbackInfo);
}
