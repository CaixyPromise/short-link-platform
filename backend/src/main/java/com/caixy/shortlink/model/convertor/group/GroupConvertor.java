package com.caixy.shortlink.model.convertor.group;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.shortlink.model.entity.Group;
import com.caixy.shortlink.model.vo.group.GroupItemVO;
import com.caixy.shortlink.model.vo.group.GroupVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 分组转化器
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/15 16:43
 */
@Mapper
public interface GroupConvertor
{
    GroupConvertor INSTANCE = Mappers.getMapper(GroupConvertor.class);

    /**
    * 将实体类列表转化为VO列表
    */
    List<GroupVO> copyVOList(List<Group> sourceList);
    /**
    * 将实体类转化为VO
    */
    GroupVO copyVO(Group source);

    Page<GroupVO> copyVOPage(Page<Group> sourcePage);
}
