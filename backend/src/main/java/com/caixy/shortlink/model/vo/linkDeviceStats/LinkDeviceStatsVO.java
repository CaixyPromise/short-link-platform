package com.caixy.shortlink.model.vo.linkDeviceStats;

import com.caixy.shortlink.model.entity.LinkDeviceStats;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接设备统计视图
 */
@Data
public class LinkDeviceStatsVO implements Serializable {

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
     * @param linkDeviceStatsVO
     * @return
     */
    public static LinkDeviceStats voToObj(LinkDeviceStatsVO linkDeviceStatsVO) {
        if (linkDeviceStatsVO == null) {
            return null;
        }
        LinkDeviceStats linkDeviceStats = new LinkDeviceStats();
        BeanUtils.copyProperties(linkDeviceStatsVO, linkDeviceStats);
        return linkDeviceStats;
    }

    /**
     * 对象转封装类
     *
     * @param linkDeviceStats
     * @return
     */
    public static LinkDeviceStatsVO objToVo(LinkDeviceStats linkDeviceStats) {
        if (linkDeviceStats == null) {
            return null;
        }
        LinkDeviceStatsVO linkDeviceStatsVO = new LinkDeviceStatsVO();
        BeanUtils.copyProperties(linkDeviceStats, linkDeviceStatsVO);
        return linkDeviceStatsVO;
    }
}
