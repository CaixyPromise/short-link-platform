package com.caixy.shortlink.model.dto.link;

import com.caixy.shortlink.common.PageRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 查询短链接信息请求
 * @author: CAIXYPROMISE
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LinkQueryRequest extends PageRequest implements Serializable {

    /**
    * 分组id
    */
    @NotBlank
    private String gid;

    @Serial
    private static final long serialVersionUID = 1L;
}