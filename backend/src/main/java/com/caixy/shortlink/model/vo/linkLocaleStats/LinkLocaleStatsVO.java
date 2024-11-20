package com.caixy.shortlink.model.vo.linkLocaleStats;

import com.caixy.shortlink.model.entity.LinkLocaleStats;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接地域统计视图
 */
@Data
public class LinkLocaleStatsVO implements Serializable {

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
     * @param linkLocaleStatsVO
     * @return
     */
    public static LinkLocaleStats voToObj(LinkLocaleStatsVO linkLocaleStatsVO) {
        if (linkLocaleStatsVO == null) {
            return null;
        }
        LinkLocaleStats linkLocaleStats = new LinkLocaleStats();
        BeanUtils.copyProperties(linkLocaleStatsVO, linkLocaleStats);
        return linkLocaleStats;
    }

    /**
     * 对象转封装类
     *
     * @param linkLocaleStats
     * @return
     */
    public static LinkLocaleStatsVO objToVo(LinkLocaleStats linkLocaleStats) {
        if (linkLocaleStats == null) {
            return null;
        }
        LinkLocaleStatsVO linkLocaleStatsVO = new LinkLocaleStatsVO();
        BeanUtils.copyProperties(linkLocaleStats, linkLocaleStatsVO);
        return linkLocaleStatsVO;
    }
}
