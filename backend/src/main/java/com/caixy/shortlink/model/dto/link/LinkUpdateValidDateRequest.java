package com.caixy.shortlink.model.dto.link;

import com.caixy.shortlink.common.BaseSerializablePayload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 更新链接有效时间的请求体
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LinkUpdateValidDateRequest extends BaseSerializablePayload
{
    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    @NotNull
    @Max(value = 1, message = "有效期类型不正确")
    @Min(value = 0, message = "有效期类型不正确")
    private Integer validDateType;

    /**
     * 有效期-开始时间
     */
    private Date validDateStart;
    /**
     * 有效期-结束时间
     */
    private Date validDateEnd;

    /**
    * 分组ID
    */
    private String groupId;

    /**
    * 链接ID
    */
    private Long linkId;
}
