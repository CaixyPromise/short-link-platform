package com.caixy.shortlink.model.vo.link;

import com.caixy.shortlink.common.BaseSerializablePayload;
import com.caixy.shortlink.model.entity.Link;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接信息视图
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LinkVO extends BaseSerializablePayload
{
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
    * 短链名称
    */
    private String linkName;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 网站图标
     */
    private String favicon;

    /**
     * 启用标识 0：启用 1：未启用
     */
    private Integer enableStatus;

    /**
     * 创建类型 0：接口创建 1：控制台创建
     */
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    private Integer validDateType;

    /**
     * 有效期开始
     */
    private Date validDateStart;

    /**
    * 有效期结束
    */
    private Date validDateEnd;

    /**
     * 描述
     */
    private String description;

    /**
     * 历史PV
     */
    private Integer totalPv;

    /**
     * 历史UV
     */
    private Integer totalUv;

    /**
     * 历史UIP
     */
    private Integer totalUip;
}
