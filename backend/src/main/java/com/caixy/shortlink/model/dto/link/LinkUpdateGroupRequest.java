package com.caixy.shortlink.model.dto.link;

import com.caixy.shortlink.common.BaseSerializablePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 修改链接分组的请求
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/29 17:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LinkUpdateGroupRequest extends BaseSerializablePayload
{
    /**
    * 分组id
    */
    private String groupId;

    /**
    * 新的分组id
    */
    private String newGroupId;

    /**
    * 需要移动的链接id
    */
    private List<Long> linkIds;
}
