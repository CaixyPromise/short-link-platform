package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.shortlink.model.entity.Link;
import org.apache.ibatis.annotations.Param;

/**
 * @author CAIXYPROMISE
 * @description 针对表【t_link(短链接信息表)】的数据库操作Mapper
 * @createDate 2024-11-10 00:44:34
 * @Entity com.caixy.shortlink.model.entity.Link
 */
public interface LinkMapper extends BaseMapper<Link>
{
    /**
    * 根据短链后缀查询链接信息
    */
    Link findShortLinkBySuffix(@Param("suffix") String suffix);
    /**
    * 根据gid和nickName查询分页短链接列表
    */
    IPage<Link> queryLinksByGidAndNickName(Page<Link> page, @Param("gid") String gid, @Param("nickName") String nickName);

    int updateGidByOldGid(@Param("oldGid") String oldGid, @Param("newGid") String newGid);
}




