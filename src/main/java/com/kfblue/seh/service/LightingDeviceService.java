package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.LightingDevice;
import com.kfblue.seh.mapper.LightingDeviceMapper;
import com.kfblue.seh.dto.DeviceStatisticsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 照明设备服务类
 * 
 * @author system
 * @since 2025-01-27
 */
@Service
@RequiredArgsConstructor
public class LightingDeviceService extends ServiceImpl<LightingDeviceMapper, LightingDevice> {

    private final LightingDeviceMapper lightingDeviceMapper;

    /**
     * 分页查询照明设备
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param deviceName 设备名称
     * @param regionId 区域ID
     * @param onlineStatus 在线状态
     * @return 分页结果
     */
    public IPage<LightingDevice> page(Long current, Long size, String deviceName, Long regionId, Integer onlineStatus) {
        Page<LightingDevice> page = new Page<>(current, size);
        LambdaQueryWrapper<LightingDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(deviceName != null && !deviceName.trim().isEmpty(), LightingDevice::getDeviceName, deviceName)
                .eq(regionId != null, LightingDevice::getRegionId, regionId)
                .eq(onlineStatus != null, LightingDevice::getOnlineStatus, onlineStatus)
                .eq(LightingDevice::getDeleted, 0)
                .orderByDesc(LightingDevice::getCreatedAt);
        return this.page(page, wrapper);
    }

    /**
     * 根据区域ID查询照明设备
     * 
     * @param regionId 区域ID
     * @return 照明设备列表
     */
    public List<LightingDevice> getByRegionId(Long regionId) {
        return lightingDeviceMapper.selectByRegionId(regionId);
    }
    
    /**
     * 根据区域ID查询照明设备（字符串参数）
     * 
     * @param regionId 区域ID
     * @return 照明设备列表
     */
    public List<LightingDevice> getDevicesByRegionId(String regionId) {
        LambdaQueryWrapper<LightingDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LightingDevice::getRegionId, regionId)
                .eq(LightingDevice::getDeleted, 0)
                .orderByDesc(LightingDevice::getCreatedAt);
        return this.list(wrapper);
    }
    
    /**
     * 分页查询指定区域的照明设备
     * 
     * @param page 分页参数
     * @param regionId 区域ID
     * @return 分页结果
     */
    public IPage<LightingDevice> getDevicesByRegion(Page<LightingDevice> page, String regionId) {
        LambdaQueryWrapper<LightingDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LightingDevice::getRegionId, regionId)
                .eq(LightingDevice::getDeleted, 0)
                .orderByDesc(LightingDevice::getCreatedAt);
        return this.page(page, wrapper);
    }
    
    /**
     * 根据设备编码查询设备
     * 
     * @param deviceCode 设备编码
     * @return 照明设备
     */
    public LightingDevice getByDeviceCode(String deviceCode) {
        LambdaQueryWrapper<LightingDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LightingDevice::getDeviceCode, deviceCode)
                .eq(LightingDevice::getDeleted, 0);
        return this.getOne(wrapper);
    }

    /**
     * 获取设备统计信息（DTO格式）
     * 
     * @return 设备统计数据
     */
    public DeviceStatisticsDTO getDeviceStatisticsDTO() {
        return lightingDeviceMapper.getAllDeviceStatistics();
    }
    
    /**
     * 获取设备统计信息（Map格式）
     * 
     * @return 设备统计数据
     */
    public Map<String, Object> getDeviceStatistics() {
        LambdaQueryWrapper<LightingDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LightingDevice::getDeleted, 0);
        List<LightingDevice> devices = this.list(wrapper);
        
        Map<String, Object> statistics = new HashMap<>();
        int total = devices.size();
        int online = 0;
        int working = 0;
        int alarm = 0;
        
        for (LightingDevice device : devices) {
            if (device.getOnlineStatus() != null && device.getOnlineStatus() == 1) {
                online++;
            }
            if (device.getWorkStatus() != null && device.getWorkStatus() == 1) {
                working++;
            }
            if (device.getAlarmStatus() != null && device.getAlarmStatus() == 1) {
                alarm++;
            }
        }
        
        statistics.put("total", total);
        statistics.put("online", online);
        statistics.put("offline", total - online);
        statistics.put("working", working);
        statistics.put("alarm", alarm);
        statistics.put("onlineRate", total > 0 ? (double) online / total : 0);
        statistics.put("offlineRate", total > 0 ? (double) (total - online) / total : 0);
        statistics.put("alarmRate", total > 0 ? (double) alarm / total : 0);
        
        return statistics;
    }

    /**
     * 根据设备类型获取设备统计信息
     * 
     * @param deviceType 设备类型(single_light:单灯,light_box:灯箱)
     * @return 设备统计数据
     */
    public DeviceStatisticsDTO getDeviceStatisticsByType(String deviceType) {
        return lightingDeviceMapper.getDeviceStatisticsByType(deviceType);
    }
    
    /**
     * 根据设备类型获取设备统计信息（Map格式）
     * 
     * @param deviceType 设备类型
     * @return 设备统计数据
     */
    public Map<String, Object> getStatisticsByDeviceType(String deviceType) {
        LambdaQueryWrapper<LightingDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LightingDevice::getDeviceType, deviceType)
                .eq(LightingDevice::getDeleted, 0);
        List<LightingDevice> devices = this.list(wrapper);
        
        Map<String, Object> statistics = new HashMap<>();
        int total = devices.size();
        int online = 0;
        int working = 0;
        int alarm = 0;
        
        for (LightingDevice device : devices) {
            if (device.getOnlineStatus() != null && device.getOnlineStatus() == 1) {
                online++;
            }
            if (device.getWorkStatus() != null && device.getWorkStatus() == 1) {
                working++;
            }
            if (device.getAlarmStatus() != null && device.getAlarmStatus() == 1) {
                alarm++;
            }
        }
        
        statistics.put("total", total);
        statistics.put("online", online);
        statistics.put("offline", total - online);
        statistics.put("working", working);
        statistics.put("alarm", alarm);
        statistics.put("deviceType", deviceType);
        statistics.put("onlineRate", total > 0 ? (double) online / total : 0);
        statistics.put("offlineRate", total > 0 ? (double) (total - online) / total : 0);
        statistics.put("alarmRate", total > 0 ? (double) alarm / total : 0);
        
        return statistics;
    }

    /**
     * 更新设备工作状态
     * 
     * @param deviceId 设备ID
     * @param workStatus 工作状态(0:关闭, 1:开启)
     * @return 是否更新成功
     */
    public boolean updateWorkStatus(Long deviceId, Integer workStatus) {
        LambdaQueryWrapper<LightingDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LightingDevice::getId, deviceId)
                .eq(LightingDevice::getDeleted, 0);
        
        LightingDevice device = this.getOne(wrapper);
        if (device == null) {
            return false;
        }

        device.setWorkStatus(workStatus);
        return this.updateById(device);
    }
    
    /**
     * 根据设备编码更新设备工作状态
     * 
     * @param deviceCode 设备编码
     * @param workStatus 工作状态(0:关闭, 1:开启)
     * @return 是否更新成功
     */
    public boolean updateWorkStatus(String deviceCode, Integer workStatus) {
        LambdaQueryWrapper<LightingDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LightingDevice::getDeviceCode, deviceCode)
                .eq(LightingDevice::getDeleted, 0);
        
        LightingDevice device = this.getOne(wrapper);
        if (device == null) {
            return false;
        }
        
        // 检查设备是否在线
        if (device.getOnlineStatus() == null || device.getOnlineStatus() != 1) {
            return false;
        }
        
        device.setWorkStatus(workStatus);
        return this.updateById(device);
    }



}