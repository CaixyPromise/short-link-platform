package com.caixy.shortlink.model.vo.linkStatsToday;

import com.caixy.shortlink.model.entity.LinkStatsToday;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接当日统计视图
 */
@Data
public class LinkStatsTodayVO implements Serializable {

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
     * @param linkStatsTodayVO
     * @return
     */
    public static LinkStatsToday voToObj(LinkStatsTodayVO linkStatsTodayVO) {
        if (linkStatsTodayVO == null) {
            return null;
        }
        LinkStatsToday linkStatsToday = new LinkStatsToday();
        BeanUtils.copyProperties(linkStatsTodayVO, linkStatsToday);
        return linkStatsToday;
    }

    /**
     * 对象转封装类
     *
     * @param linkStatsToday
     * @return
     */
    public static LinkStatsTodayVO objToVo(LinkStatsToday linkStatsToday) {
        if (linkStatsToday == null) {
            return null;
        }
        LinkStatsTodayVO linkStatsTodayVO = new LinkStatsTodayVO();
        BeanUtils.copyProperties(linkStatsToday, linkStatsTodayVO);
        return linkStatsTodayVO;
    }
}
