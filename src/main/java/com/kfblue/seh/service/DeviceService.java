package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.Device;
import com.kfblue.seh.mapper.DeviceMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备服务实现类
 */
@Service
public class DeviceService extends ServiceImpl<DeviceMapper, Device> {

    public Page<Device> page(Page<Device> page, LambdaQueryWrapper<Device> queryWrapper) {
        return super.page(page, queryWrapper);
    }

    public Device getByDeviceCode(String deviceCode) {
        return baseMapper.selectByDeviceCode(deviceCode);
    }

    public List<Device> getByRegionId(Long regionId) {
        return baseMapper.selectByRegionId(regionId);
    }

    public List<Device> getByDeviceType(String deviceType) {
        return baseMapper.selectByDeviceType(deviceType);
    }

    public List<Device> getByOnlineStatus(Integer onlineStatus) {
        return baseMapper.selectByOnlineStatus(onlineStatus);
    }

    public List<Map<String, Integer>> getOnlineStats() {
        return baseMapper.selectOnlineStats();
    }

    public boolean createDevice(Device device) {
        return super.save(device);
    }

    public boolean updateDevice(Device device) {
        return super.updateById(device);
    }

    public boolean deleteDevice(Long id) {
        return super.removeById(id);
    }

    public boolean batchUpdateStatus(List<Long> deviceIds, Integer status) {
        // 实现批量更新设备状态逻辑
        return false;
    }

    public boolean updateOnlineStatus(Long id, Integer onlineStatus, LocalDateTime lastOnlineTime) {
        return baseMapper.updateOnlineStatus(id, onlineStatus, lastOnlineTime) > 0;
    }

    public List<Device> checkOfflineDevices(Integer offlineThresholdMinutes) {
        // 实现离线设备检测逻辑
        return List.of();
    }

    public boolean isDeviceCodeUnique(String deviceCode, Long excludeId) {
        // 实现设备编码唯一性校验逻辑
        return true;
    }

    public List<Map<String, Integer>> countByDeviceType() {
        return baseMapper.countByDeviceType();
    }
}