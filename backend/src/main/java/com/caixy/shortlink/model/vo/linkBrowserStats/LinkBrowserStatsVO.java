package com.caixy.shortlink.model.vo.linkBrowserStats;

import com.caixy.shortlink.model.entity.LinkBrowserStats;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接浏览器统计视图
 */
@Data
public class LinkBrowserStatsVO implements Serializable {

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
     * @param linkBrowserStatsVO
     * @return
     */
    public static LinkBrowserStats voToObj(LinkBrowserStatsVO linkBrowserStatsVO) {
        if (linkBrowserStatsVO == null) {
            return null;
        }
        LinkBrowserStats linkBrowserStats = new LinkBrowserStats();
        BeanUtils.copyProperties(linkBrowserStatsVO, linkBrowserStats);
        return linkBrowserStats;
    }

    /**
     * 对象转封装类
     *
     * @param linkBrowserStats
     * @return
     */
    public static LinkBrowserStatsVO objToVo(LinkBrowserStats linkBrowserStats) {
        if (linkBrowserStats == null) {
            return null;
        }
        LinkBrowserStatsVO linkBrowserStatsVO = new LinkBrowserStatsVO();
        BeanUtils.copyProperties(linkBrowserStats, linkBrowserStatsVO);
        return linkBrowserStatsVO;
    }
}
