package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.shortlink.model.entity.Group;
import com.caixy.shortlink.model.vo.group.GroupItemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【t_group(分组信息表)】的数据库操作Mapper
 * @createDate 2024-11-10 00:44:34
 * @Entity com.caixy.shortlink.model.entity.Group
 */
public interface GroupMapper extends BaseMapper<Group>
{
    Group findGroupByGid(@Param("gid") String gid);
    List<GroupItemVO> findGroupsWithLinkCounts(@Param("nickName") String nickName);
}




