package com.caixy.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caixy.shortlink.model.dto.linkAccessLogs.ShortLinkGroupStatsAccessRecordReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkGroupStatsReqDTO;
import com.caixy.shortlink.model.dto.linkAccessStats.ShortLinkStatsReqDTO;
import com.caixy.shortlink.model.entity.LinkAccessLogs;
import com.caixy.shortlink.model.entity.LinkAccessStats;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author CAIXYPROMISE
* @description 针对表【t_link_access_logs(短链接访问日志表)】的数据库操作Mapper
* @createDate 2024-11-10 00:44:34
* @Entity com.caixy.shortlink.model.entity.LinkAccessLogs
*/
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogs> {

    /**
     * 根据短链接获取指定日期内高频访问IP数据
     */
    List<HashMap<String, Object>> listTopIpByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内高频访问IP数据
     */
    List<HashMap<String, Object>> listTopIpByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内新旧访客数据
     */
    HashMap<String, Object> findUvTypeCntByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
    /**
     * 获取用户信息是否新老访客
     *
     * @param gid               分组ID
     * @param fullShortUrl      短链接完整URL
     * @param enableStatus      启用状态
     * @param startDate         开始日期
     * @param endDate           结束日期
     * @param userAccessLogsList 用户访问日志列表
     * @return 用户新老访客信息列表
     */
    @MapKey("user")
    List<Map<String, Object>> selectUvTypeByUsers(
            @Param("gid") String gid,
            @Param("fullShortUrl") String fullShortUrl,
            @Param("enableStatus") Integer enableStatus,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userAccessLogsList") List<String> userAccessLogsList
    );

    /**
     * 获取分组用户信息是否新老访客
     *
     * @param gid               分组ID
     * @param startDate         开始日期
     * @param endDate           结束日期
     * @param userAccessLogsList 用户访问日志列表
     * @return 分组用户新老访客信息列表
     */
    @MapKey("user")
    List<Map<String, Object>> selectGroupUvTypeByUsers(
            @Param("gid") String gid,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userAccessLogsList") List<String> userAccessLogsList
    );

    /**
     * 根据短链接获取指定日期内PV、UV、UIP数据
     */
    LinkAccessStats findPvUvUidStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内PV、UV、UIP数据
     */
    LinkAccessStats findPvUvUidStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    IPage<LinkAccessLogs> selectGroupPage(@Param("param") ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}




