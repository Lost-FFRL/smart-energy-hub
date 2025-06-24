package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.PumpDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 水泵设备Mapper接口
 * 
 * @author system
 * @since 2025-06-24
 */
@Mapper
public interface PumpDeviceMapper extends BaseMapper<PumpDevice> {

    /**
     * 根据区域ID查询水泵设备
     * 
     * @param regionId 区域ID
     * @return 水泵设备列表
     */
    @Select("SELECT * FROM pump_devices WHERE region_id = #{regionId} AND deleted = 0")
    List<PumpDevice> selectByRegionId(@Param("regionId") Long regionId);

    /**
     * 获取设备统计信息
     * 
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) as total, " +
            "SUM(CASE WHEN online_status = 1 THEN 1 ELSE 0 END) as onlineCount, " +
            "SUM(CASE WHEN work_status = 1 THEN 1 ELSE 0 END) as runningCount, " +
            "SUM(CASE WHEN alarm_status = 1 THEN 1 ELSE 0 END) as alarmCount " +
            "FROM pump_devices WHERE deleted = 0")
    PumpDeviceStatistics getDeviceStatistics();

    /**
     * 设备统计结果内部类
     */
    class PumpDeviceStatistics {
        private Integer total;
        private Integer onlineCount;
        private Integer runningCount;
        private Integer alarmCount;

        // getters and setters
        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }
        public Integer getOnlineCount() { return onlineCount; }
        public void setOnlineCount(Integer onlineCount) { this.onlineCount = onlineCount; }
        public Integer getRunningCount() { return runningCount; }
        public void setRunningCount(Integer runningCount) { this.runningCount = runningCount; }
        public Integer getAlarmCount() { return alarmCount; }
        public void setAlarmCount(Integer alarmCount) { this.alarmCount = alarmCount; }
    }
}