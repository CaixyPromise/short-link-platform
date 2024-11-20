package com.caixy.shortlink.model.dto.link;

import com.caixy.shortlink.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private String gid;

    private static final long serialVersionUID = 1L;
}