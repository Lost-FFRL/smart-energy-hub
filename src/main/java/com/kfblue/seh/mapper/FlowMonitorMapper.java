package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.FlowMonitor;
import com.kfblue.seh.vo.SimpleFlowDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流量监控Mapper接口
 *
 * @author system
 * @since 2025-06-24
 */
@Mapper
public interface FlowMonitorMapper extends BaseMapper<FlowMonitor> {

    /**
     * 根据设备ID和时间范围查询流量数据
     *
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 流量监控数据列表
     */
    @Select("SELECT * FROM flow_monitor WHERE device_id = #{deviceId} " +
            "AND monitor_time BETWEEN #{startTime} AND #{endTime} " +
            "AND deleted = 0 ORDER BY monitor_time DESC")
    List<FlowMonitor> selectByDeviceIdAndTimeRange(@Param("deviceId") Long deviceId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 获取设备最新流量数据
     *
     * @param deviceId 设备ID
     * @return 最新流量数据
     */
    @Select("SELECT * FROM flow_monitor WHERE device_id = #{deviceId} " +
            "AND deleted = 0 ORDER BY monitor_time DESC LIMIT 1")
    FlowMonitor selectLatestByDeviceId(@Param("deviceId") Long deviceId);

    /**
     * 获取小时流量统计数据
     *
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 小时流量统计列表
     */
    @org.apache.ibatis.annotations.Select("""
             SELECT 
            DATE_FORMAT(monitor_time, '%Y-%m-%d %H') as time, 
            SUM(cumulative_flow) as value 
            FROM flow_monitor 
            WHERE device_id = #{deviceId} 
            AND monitor_time BETWEEN #{startTime} AND #{endTime} 
            AND deleted = 0 
            GROUP BY DATE_FORMAT(monitor_time, '%Y-%m-%d %H')
            ORDER BY time 
            """)
    List<SimpleFlowDataVO> getHourlyFlowStatistics(@Param("deviceId") Long deviceId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 小时流量统计结果内部类
     */
    class HourlyFlowStatistics {
        private String hour;
        private Double totalFlow;

        // getters and setters
        public String getHour() { return hour; }
        public void setHour(String hour) { this.hour = hour; }
        public Double getTotalFlow() { return totalFlow; }
        public void setTotalFlow(Double totalFlow) { this.totalFlow = totalFlow; }
    }
}