package com.caixy.shortlink.model.vo.linkNetworkStats;

import com.caixy.shortlink.model.entity.LinkNetworkStats;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接网络统计视图
 */
@Data
public class LinkNetworkStatsVO implements Serializable {

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
     * @param linkNetworkStatsVO
     * @return
     */
    public static LinkNetworkStats voToObj(LinkNetworkStatsVO linkNetworkStatsVO) {
        if (linkNetworkStatsVO == null) {
            return null;
        }
        LinkNetworkStats linkNetworkStats = new LinkNetworkStats();
        BeanUtils.copyProperties(linkNetworkStatsVO, linkNetworkStats);
        return linkNetworkStats;
    }

    /**
     * 对象转封装类
     *
     * @param linkNetworkStats
     * @return
     */
    public static LinkNetworkStatsVO objToVo(LinkNetworkStats linkNetworkStats) {
        if (linkNetworkStats == null) {
            return null;
        }
        LinkNetworkStatsVO linkNetworkStatsVO = new LinkNetworkStatsVO();
        BeanUtils.copyProperties(linkNetworkStats, linkNetworkStatsVO);
        return linkNetworkStatsVO;
    }
}
