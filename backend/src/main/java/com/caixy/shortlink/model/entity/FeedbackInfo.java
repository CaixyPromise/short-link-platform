package com.caixy.shortlink.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.caixy.shortlink.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 反馈信息表
 * @TableName t_feedback_info
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_feedback_info")
@SuperBuilder(toBuilder = true)
@Data
public class FeedbackInfo extends BaseEntity  {

    /**
     * 创建人id
     */
    private Long creatorId;

    /**
     * 反馈标题
     */
    private String title;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 反馈者邮箱
     */
    private String contactEmail;

    /**
     * 反馈者姓名
     */
    private String contactName;

    /**
     * 是否解决
     */
    private Integer isSolve;

    /**
     * 回复内容
     */
    private String replayContent;

    /**
     * 回复时间
     */
    private Date replayTime;
}