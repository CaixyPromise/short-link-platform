package com.caixy.shortlink.model.vo.linkAccessStats;

import com.caixy.shortlink.model.entity.LinkAccessStats;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接访问统计视图
 */
@Data
public class LinkAccessStatsVO implements Serializable {

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
     * @param linkAccessStatsVO
     * @return
     */
    public static LinkAccessStats voToObj(LinkAccessStatsVO linkAccessStatsVO) {
        if (linkAccessStatsVO == null) {
            return null;
        }
        LinkAccessStats linkAccessStats = new LinkAccessStats();
        BeanUtils.copyProperties(linkAccessStatsVO, linkAccessStats);
        return linkAccessStats;
    }

    /**
     * 对象转封装类
     *
     * @param linkAccessStats
     * @return
     */
    public static LinkAccessStatsVO objToVo(LinkAccessStats linkAccessStats) {
        if (linkAccessStats == null) {
            return null;
        }
        LinkAccessStatsVO linkAccessStatsVO = new LinkAccessStatsVO();
        BeanUtils.copyProperties(linkAccessStats, linkAccessStatsVO);
        return linkAccessStatsVO;
    }
}
