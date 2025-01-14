package com.caixy.shortlink.model.vo.feedback;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 用户反馈视图
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/14 15:55
 */
@Data
@Builder
public class UserFeedbackInfoVO
{
    /**
     * 反馈标题
     */
    private String title;

    /**
     * 反馈邮箱
     */
    private String contactEmail;

    /**
     * 反馈姓名
     */
    private String contactName;

    /**
     * 是否解决
     */
    private Integer isSolve;

    /**
     * 回复时间
     */
    private Date replayTime;
}
