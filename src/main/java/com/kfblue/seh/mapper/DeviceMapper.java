package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备Mapper接口
 * 
 * @author system
 * @since 2025-06-17
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {

    /**
     * 根据设备编码查询设备
     * 
     * @param deviceCode 设备编码
     * @return 设备信息
     */
    @Select("SELECT * FROM devices WHERE device_code = #{deviceCode} AND deleted = 0")
    Device selectByDeviceCode(@Param("deviceCode") String deviceCode);

    /**
     * 根据区域ID查询设备列表
     * 
     * @param regionId 区域ID
     * @return 设备列表
     */
    @Select("SELECT * FROM devices WHERE region_id = #{regionId} AND deleted = 0 ORDER BY device_code")
    List<Device> selectByRegionId(@Param("regionId") Long regionId);

    /**
     * 根据设备类型查询设备列表
     * 
     * @param deviceType 设备类型
     * @return 设备列表
     */
    @Select("SELECT * FROM devices WHERE device_type = #{deviceType} AND deleted = 0 ORDER BY device_code")
    List<Device> selectByDeviceType(@Param("deviceType") String deviceType);

    /**
     * 根据在线状态查询设备列表
     * 
     * @param onlineStatus 在线状态
     * @return 设备列表
     */
    @Select("SELECT * FROM devices WHERE online_status = #{onlineStatus} AND deleted = 0 ORDER BY device_code")
    List<Device> selectByOnlineStatus(@Param("onlineStatus") Integer onlineStatus);

    /**
     * 更新设备在线状态
     * 
     * @param deviceId 设备ID
     * @param onlineStatus 在线状态
     * @param lastOnlineTime 最后在线时间
     * @return 更新行数
     */
    @Update("UPDATE devices SET online_status = #{onlineStatus}, last_online_time = #{lastOnlineTime} WHERE id = #{deviceId}")
    int updateOnlineStatus(@Param("deviceId") Long deviceId, 
                          @Param("onlineStatus") Integer onlineStatus, 
                          @Param("lastOnlineTime") LocalDateTime lastOnlineTime);

    /**
     * 查询设备在线统计
     * 
     * @return 在线统计信息
     */
    @Select("SELECT online_status, COUNT(*) as count FROM devices WHERE deleted = 0 GROUP BY online_status")
    List<java.util.Map<String, Integer>> selectOnlineStats();

    /**
     * 按设备类型分组统计设备数量
     * @return 设备类型及数量列表
     */
    @Select("SELECT device_type, COUNT(*) as count FROM devices WHERE deleted = 0 GROUP BY device_type")
    List<java.util.Map<String, Integer>> countByDeviceType();
}