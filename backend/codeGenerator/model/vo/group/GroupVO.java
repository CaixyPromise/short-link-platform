package com.caixy.shortlink.model.vo.group;

import com.caixy.shortlink.model.entity.Group;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 分组信息视图
 */
@Data
public class GroupVO implements Serializable {

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
     * @param groupVO
     * @return
     */
    public static Group voToObj(GroupVO groupVO) {
        if (groupVO == null) {
            return null;
        }
        Group group = new Group();
        BeanUtils.copyProperties(groupVO, group);
        return group;
    }

    /**
     * 对象转封装类
     *
     * @param group
     * @return
     */
    public static GroupVO objToVo(Group group) {
        if (group == null) {
            return null;
        }
        GroupVO groupVO = new GroupVO();
        BeanUtils.copyProperties(group, groupVO);
        return groupVO;
    }
}
