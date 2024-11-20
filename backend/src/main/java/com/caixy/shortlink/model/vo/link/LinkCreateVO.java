package com.caixy.shortlink.model.vo.link;

import com.caixy.shortlink.common.BaseSerializablePayload;
import lombok.*;

/**
 * 短链接创建结果视图
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/20 1:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinkCreateVO extends BaseSerializablePayload
{
    /**
    * 短链接
    */
    private String shortLink;
    /**
    * 分组id
    */
    private String gid;
    /**
    * 原始链接
    */
    private String originUrl;
}
