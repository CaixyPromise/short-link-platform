package com.caixy.shortlink.model.vo.linkOsStats;

import com.caixy.shortlink.model.entity.LinkOsStats;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接操作系统统计视图
 */
@Data
public class LinkOsStatsVO implements Serializable {

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
     * @param linkOsStatsVO
     * @return
     */
    public static LinkOsStats voToObj(LinkOsStatsVO linkOsStatsVO) {
        if (linkOsStatsVO == null) {
            return null;
        }
        LinkOsStats linkOsStats = new LinkOsStats();
        BeanUtils.copyProperties(linkOsStatsVO, linkOsStats);
        return linkOsStats;
    }

    /**
     * 对象转封装类
     *
     * @param linkOsStats
     * @return
     */
    public static LinkOsStatsVO objToVo(LinkOsStats linkOsStats) {
        if (linkOsStats == null) {
            return null;
        }
        LinkOsStatsVO linkOsStatsVO = new LinkOsStatsVO();
        BeanUtils.copyProperties(linkOsStats, linkOsStatsVO);
        return linkOsStatsVO;
    }
}
