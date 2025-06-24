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

import java.util.List;

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
     * 获取设备统计信息
     * 
     * @return 设备统计数据
     */
    public DeviceStatisticsDTO getDeviceStatistics() {
        return lightingDeviceMapper.getAllDeviceStatistics();
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
        
        // 检查设备是否在线
        if (device.getOnlineStatus() == null || device.getOnlineStatus() != 1) {
            return false;
        }
        
        device.setWorkStatus(workStatus);
        return this.updateById(device);
    }



}