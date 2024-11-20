package com.caixy.shortlink.model.vo.link;

import com.caixy.shortlink.model.entity.Link;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接信息视图
 */
@Data
public class LinkVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param linkVO
     * @return
     */
    public static Link voToObj(LinkVO linkVO) {
        if (linkVO == null) {
            return null;
        }
        Link link = new Link();
        BeanUtils.copyProperties(linkVO, link);
        return link;
    }

    /**
     * 对象转封装类
     *
     * @param link
     * @return
     */
    public static LinkVO objToVo(Link link) {
        if (link == null) {
            return null;
        }
        LinkVO linkVO = new LinkVO();
        BeanUtils.copyProperties(link, linkVO);
        return linkVO;
    }
}
