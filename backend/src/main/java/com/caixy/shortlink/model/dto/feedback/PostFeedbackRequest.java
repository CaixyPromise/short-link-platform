package com.caixy.shortlink.model.dto.feedback;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 添加反馈请求
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/14 15:40
 */
@Data
public class PostFeedbackRequest implements Serializable
{
    /**
     * 反馈标题
     */
    @NotBlank
    @Size(min = 1, max = 30)
    private String title;

    /**
     * 反馈内容
     */
    @NotBlank
    @Size(min = 1, max = 512)
    private String content;

    /**
     * 反馈者邮箱
     */
    @NotBlank
    @Size(min = 1, max = 128)
    @Email
    private String contactEmail;

    /**
     * 反馈者姓名
     */
    @NotBlank
    @Size(min = 2, max = 30)
    private String contactName;

    @Serial
    private static final long serialVersionUID = 1L;
}
