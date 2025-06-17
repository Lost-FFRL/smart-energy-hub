package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.DeviceDailyStatistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 设备日统计Mapper接口
 * 
 * @author system
 * @since 2025-06-17
 */
@Mapper
public interface DeviceDailyStatisticMapper extends BaseMapper<DeviceDailyStatistic> {

    /**
     * 根据设备ID和日期查询统计
     * 
     * @param deviceId 设备ID
     * @param statDate 统计日期
     * @return 统计信息
     */
    @Select("SELECT * FROM device_daily_statistics WHERE device_id = #{deviceId} " +
            "AND stat_date = #{statDate} AND deleted = 0")
    DeviceDailyStatistic selectByDeviceIdAndDate(@Param("deviceId") Long deviceId,
                                                 @Param("statDate") LocalDate statDate);

    /**
     * 根据设备ID和日期范围查询统计列表
     * 
     * @param deviceId 设备ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计列表
     */
    @Select("SELECT * FROM device_daily_statistics WHERE device_id = #{deviceId} " +
            "AND stat_date BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0 ORDER BY stat_date")
    List<DeviceDailyStatistic> selectByDeviceIdAndDateRange(@Param("deviceId") Long deviceId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    /**
     * 根据日期查询所有设备统计
     * 
     * @param statDate 统计日期
     * @return 统计列表
     */
    @Select("SELECT * FROM device_daily_statistics WHERE stat_date = #{statDate} " +
            "AND deleted = 0 ORDER BY device_id")
    List<DeviceDailyStatistic> selectByDate(@Param("statDate") LocalDate statDate);

    /**
     * 查询设备月度消耗统计
     * 
     * @param deviceId 设备ID
     * @param year 年份
     * @param month 月份
     * @return 月度统计
     */
    @Select("SELECT " +
            "SUM(daily_consumption) as total_consumption, " +
            "AVG(daily_consumption) as avg_consumption, " +
            "MAX(daily_consumption) as max_consumption, " +
            "MIN(daily_consumption) as min_consumption, " +
            "COUNT(*) as stat_days " +
            "FROM device_daily_statistics " +
            "WHERE device_id = #{deviceId} " +
            "AND YEAR(stat_date) = #{year} " +
            "AND MONTH(stat_date) = #{month} " +
            "AND deleted = 0")
    java.util.Map<String, Object> selectMonthlyStats(@Param("deviceId") Long deviceId,
                                                     @Param("year") Integer year,
                                                     @Param("month") Integer month);

    /**
     * 查询设备年度消耗统计
     * 
     * @param deviceId 设备ID
     * @param year 年份
     * @return 年度统计
     */
    @Select("SELECT " +
            "SUM(daily_consumption) as total_consumption, " +
            "AVG(daily_consumption) as avg_consumption, " +
            "MAX(daily_consumption) as max_consumption, " +
            "MIN(daily_consumption) as min_consumption, " +
            "COUNT(*) as stat_days " +
            "FROM device_daily_statistics " +
            "WHERE device_id = #{deviceId} " +
            "AND YEAR(stat_date) = #{year} " +
            "AND deleted = 0")
    java.util.Map<String, Object> selectYearlyStats(@Param("deviceId") Long deviceId,
                                                    @Param("year") Integer year);

    /**
     * 查询设备在线率排行
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制条数
     * @return 在线率排行
     */
    @Select("SELECT device_id, AVG(online_rate) as avg_online_rate " +
            "FROM device_daily_statistics " +
            "WHERE stat_date BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0 " +
            "GROUP BY device_id " +
            "ORDER BY avg_online_rate DESC " +
            "LIMIT #{limit}")
    List<java.util.Map<String, Object>> selectOnlineRateRanking(@Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate,
                                                               @Param("limit") Integer limit);

    /**
     * 获取设备消耗趋势
     * 
     * @param deviceId 设备ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 消耗趋势数据
     */
    @Select("SELECT stat_date, daily_consumption " +
            "FROM device_daily_statistics " +
            "WHERE device_id = #{deviceId} " +
            "AND stat_date BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0 " +
            "ORDER BY stat_date")
    List<java.util.Map<String, Object>> getDeviceConsumptionTrend(@Param("deviceId") Long deviceId,
                                                                 @Param("startDate") LocalDate startDate,
                                                                 @Param("endDate") LocalDate endDate);

    /**
     * 获取区域消耗统计
     * 
     * @param regionId 区域ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 区域消耗统计
     */
    @Select("SELECT d.region_id, SUM(dds.daily_consumption) as total_consumption, " +
            "AVG(dds.daily_consumption) as avg_consumption " +
            "FROM device_daily_statistics dds " +
            "JOIN devices d ON dds.device_id = d.id " +
            "WHERE d.region_id = #{regionId} " +
            "AND dds.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "AND dds.deleted = 0 AND d.deleted = 0 " +
            "GROUP BY d.region_id")
    List<java.util.Map<String, Object>> getRegionConsumptionStats(@Param("regionId") Long regionId,
                                                                 @Param("startDate") LocalDate startDate,
                                                                 @Param("endDate") LocalDate endDate);

    /**
     * 获取设备类型消耗统计
     * 
     * @param deviceType 设备类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 设备类型消耗统计
     */
    @Select("SELECT d.device_type, SUM(dds.daily_consumption) as total_consumption, " +
            "AVG(dds.daily_consumption) as avg_consumption, " +
            "COUNT(DISTINCT dds.device_id) as device_count " +
            "FROM device_daily_statistics dds " +
            "JOIN devices d ON dds.device_id = d.id " +
            "WHERE d.device_type = #{deviceType} " +
            "AND dds.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "AND dds.deleted = 0 AND d.deleted = 0 " +
            "GROUP BY d.device_type")
    List<java.util.Map<String, Object>> getDeviceTypeConsumptionStats(@Param("deviceType") String deviceType,
                                                                     @Param("startDate") LocalDate startDate,
                                                                     @Param("endDate") LocalDate endDate);
}