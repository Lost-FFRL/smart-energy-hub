package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.LightingDevice;
import com.kfblue.seh.dto.DeviceStatisticsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 照明设备Mapper接口
 * 
 * @author system
 * @since 2025-01-27
 */
@Mapper
public interface LightingDeviceMapper extends BaseMapper<LightingDevice> {

    /**
     * 根据区域ID查询照明设备列表
     * 
     * @param regionId 区域ID
     * @return 照明设备列表
     */
    List<LightingDevice> selectByRegionId(@Param("regionId") Long regionId);

    /**
     * 根据设备类型统计设备信息
     * 
     * @param deviceType 设备类型(single_light:单灯,light_box:灯箱)
     * @return 设备统计数据对象
     */
    @org.apache.ibatis.annotations.Select("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN online_status = 1 THEN 1 ELSE 0 END) as onlineCnt,
            SUM(CASE WHEN online_status = 0 THEN 1 ELSE 0 END) as offlineCnt,
            SUM(CASE WHEN work_status = 1 THEN 1 ELSE 0 END) as workCnt,
            SUM(CASE WHEN alarm_status = 1 THEN 1 ELSE 0 END) as alarmCnt
        FROM lighting_devices 
        WHERE deleted = 0 
        AND device_type = #{deviceType}
        """)
    DeviceStatisticsDTO getDeviceStatisticsByType(@Param("deviceType") String deviceType);

    /**
     * 统计所有设备信息
     * 
     * @return 设备统计数据对象
     */
    @org.apache.ibatis.annotations.Select("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN online_status = 1 THEN 1 ELSE 0 END) as onlineCnt,
            SUM(CASE WHEN online_status = 0 THEN 1 ELSE 0 END) as offlineCnt,
            SUM(CASE WHEN work_status = 1 THEN 1 ELSE 0 END) as workCnt,
            SUM(CASE WHEN alarm_status = 1 THEN 1 ELSE 0 END) as alarmCnt
        FROM lighting_devices 
        WHERE deleted = 0
        """)
    DeviceStatisticsDTO getAllDeviceStatistics();


}