package com.caixy.shortlink.model.convertor.link;

import com.caixy.shortlink.model.entity.Link;
import com.caixy.shortlink.model.vo.link.LinkVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 链接转化器
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/21 1:54
 */
@Mapper
public interface LinkConvertor
{
    LinkConvertor INSTANCE = Mappers.getMapper(LinkConvertor.class);

    LinkVO toVO(Link link);
    List<LinkVO> toVOList(List<Link> linkList);
}
