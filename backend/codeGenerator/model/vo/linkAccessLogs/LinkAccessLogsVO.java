package com.caixy.shortlink.model.vo.linkAccessLogs;

import com.caixy.shortlink.model.entity.LinkAccessLogs;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接访问日志视图
 */
@Data
public class LinkAccessLogsVO implements Serializable {

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
     * @param linkAccessLogsVO
     * @return
     */
    public static LinkAccessLogs voToObj(LinkAccessLogsVO linkAccessLogsVO) {
        if (linkAccessLogsVO == null) {
            return null;
        }
        LinkAccessLogs linkAccessLogs = new LinkAccessLogs();
        BeanUtils.copyProperties(linkAccessLogsVO, linkAccessLogs);
        return linkAccessLogs;
    }

    /**
     * 对象转封装类
     *
     * @param linkAccessLogs
     * @return
     */
    public static LinkAccessLogsVO objToVo(LinkAccessLogs linkAccessLogs) {
        if (linkAccessLogs == null) {
            return null;
        }
        LinkAccessLogsVO linkAccessLogsVO = new LinkAccessLogsVO();
        BeanUtils.copyProperties(linkAccessLogs, linkAccessLogsVO);
        return linkAccessLogsVO;
    }
}
