package com.caixy.shortlink.model.vo.linkGoto;

import com.caixy.shortlink.model.entity.LinkGoto;
import lombok.Data;
import com.caixy.shortlink.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短链接跳转信息视图
 */
@Data
public class LinkGotoVO implements Serializable {

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
     * @param linkGotoVO
     * @return
     */
    public static LinkGoto voToObj(LinkGotoVO linkGotoVO) {
        if (linkGotoVO == null) {
            return null;
        }
        LinkGoto linkGoto = new LinkGoto();
        BeanUtils.copyProperties(linkGotoVO, linkGoto);
        return linkGoto;
    }

    /**
     * 对象转封装类
     *
     * @param linkGoto
     * @return
     */
    public static LinkGotoVO objToVo(LinkGoto linkGoto) {
        if (linkGoto == null) {
            return null;
        }
        LinkGotoVO linkGotoVO = new LinkGotoVO();
        BeanUtils.copyProperties(linkGoto, linkGotoVO);
        return linkGotoVO;
    }
}
