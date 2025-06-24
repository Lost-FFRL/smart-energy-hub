package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.LevelMonitor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 液位监控Mapper接口
 * 
 * @author system
 * @since 2025-06-24
 */
@Mapper
public interface LevelMonitorMapper extends BaseMapper<LevelMonitor> {

    /**
     * 根据设备ID和时间范围查询液位数据
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 液位监控数据列表
     */
    @Select("SELECT * FROM level_monitor WHERE device_id = #{deviceId} " +
            "AND monitor_time BETWEEN #{startTime} AND #{endTime} " +
            "AND deleted = 0 ORDER BY monitor_time DESC")
    List<LevelMonitor> selectByDeviceIdAndTimeRange(@Param("deviceId") Long deviceId,
                                                    @Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 获取设备最新液位数据
     * 
     * @param deviceId 设备ID
     * @return 最新液位数据
     */
    @Select("SELECT * FROM level_monitor WHERE device_id = #{deviceId} " +
            "AND deleted = 0 ORDER BY monitor_time DESC LIMIT 1")
    LevelMonitor selectLatestByDeviceId(@Param("deviceId") Long deviceId);

    /**
     * 获取所有设备的最新液位数据
     * 
     * @return 最新液位数据列表
     */
    @Select("SELECT lm.* FROM level_monitor lm " +
            "INNER JOIN ( " +
            "    SELECT device_id, MAX(monitor_time) as latest_time " +
            "    FROM level_monitor " +
            "    WHERE deleted = 0 " +
            "    GROUP BY device_id " +
            ") latest ON lm.device_id = latest.device_id AND lm.monitor_time = latest.latest_time " +
            "WHERE lm.deleted = 0")
    List<LevelMonitor> selectAllLatest();

    /**
     * 获取液位报警数据
     * 
     * @param alarmStatus 报警状态
     * @return 报警数据列表
     */
    @Select("SELECT lm.* FROM level_monitor lm " +
            "INNER JOIN ( " +
            "    SELECT device_id, MAX(monitor_time) as latest_time " +
            "    FROM level_monitor " +
            "    WHERE deleted = 0 " +
            "    GROUP BY device_id " +
            ") latest ON lm.device_id = latest.device_id AND lm.monitor_time = latest.latest_time " +
            "WHERE lm.deleted = 0 AND lm.alarm_status = #{alarmStatus}")
    List<LevelMonitor> selectByAlarmStatus(@Param("alarmStatus") Integer alarmStatus);

    /**
     * 获取液位趋势数据
     * 
     * @param deviceId 设备ID
     * @param hours 小时数
     * @return 液位趋势数据
     */
    @Select("SELECT * FROM level_monitor " +
            "WHERE device_id = #{deviceId} " +
            "AND monitor_time >= DATE_SUB(NOW(), INTERVAL #{hours} HOUR) " +
            "AND deleted = 0 " +
            "ORDER BY monitor_time")
    List<LevelMonitor> selectTrendData(@Param("deviceId") Long deviceId, @Param("hours") Integer hours);
}