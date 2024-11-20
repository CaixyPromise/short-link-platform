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
 *
 */
@Data
public class GroupVO implements Serializable {

    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


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
