package com.caixy.shortlink.model.dto.feedback;

import com.caixy.shortlink.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页获取用户反馈请求
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/14 15:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageMyFeedbackRequest extends PageRequest
{
    /**
    * 反馈id
    */
    private Long id;

    /**
    * 反馈标题
    */
    private String title;
}
