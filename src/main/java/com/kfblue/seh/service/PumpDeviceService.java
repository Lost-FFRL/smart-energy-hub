package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.PumpDevice;
import com.kfblue.seh.mapper.PumpDeviceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 水泵设备服务类
 * 
 * @author system
 * @since 2025-06-24
 */
@Service
@RequiredArgsConstructor
public class PumpDeviceService extends ServiceImpl<PumpDeviceMapper, PumpDevice> {

    /**
     * 分页查询水泵设备
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param deviceName 设备名称
     * @param regionId 区域ID
     * @param onlineStatus 在线状态
     * @param workStatus 工作状态
     * @return 分页结果
     */
    public IPage<PumpDevice> page(long current, long size, String deviceName, Long regionId, 
                                  Integer onlineStatus, Integer workStatus) {
        Page<PumpDevice> page = new Page<>(current, size);
        LambdaQueryWrapper<PumpDevice> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(deviceName != null && !deviceName.trim().isEmpty(), 
                    PumpDevice::getDeviceName, deviceName)
               .eq(regionId != null, PumpDevice::getRegionId, regionId)
               .eq(onlineStatus != null, PumpDevice::getOnlineStatus, onlineStatus)
               .eq(workStatus != null, PumpDevice::getWorkStatus, workStatus)
               .eq(PumpDevice::getDeleted, 0)
               .orderByDesc(PumpDevice::getCreatedAt);
        
        return this.page(page, wrapper);
    }

    /**
     * 根据区域ID查询水泵设备
     * 
     * @param regionId 区域ID
     * @return 水泵设备列表
     */
    public List<PumpDevice> getByRegionId(Long regionId) {
        return baseMapper.selectByRegionId(regionId);
    }

    /**
     * 获取设备统计信息
     * 
     * @return 统计信息
     */
    public PumpDeviceMapper.PumpDeviceStatistics getDeviceStatistics() {
        return baseMapper.getDeviceStatistics();
    }

    /**
     * 更新设备工作状态
     * 
     * @param deviceId 设备ID
     * @param workStatus 工作状态
     * @return 是否成功
     */
    public boolean updateWorkStatus(Long deviceId, Integer workStatus) {
        LambdaQueryWrapper<PumpDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PumpDevice::getId, deviceId)
                .eq(PumpDevice::getDeleted, 0);
        
        PumpDevice device = this.getOne(wrapper);
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

    /**
     * 更新设备在线状态
     * 
     * @param deviceId 设备ID
     * @param onlineStatus 在线状态
     * @return 是否成功
     */
    public boolean updateOnlineStatus(Long deviceId, Integer onlineStatus) {
        LambdaQueryWrapper<PumpDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PumpDevice::getId, deviceId)
                .eq(PumpDevice::getDeleted, 0);
        
        PumpDevice device = this.getOne(wrapper);
        if (device == null) {
            return false;
        }
        
        device.setOnlineStatus(onlineStatus);
        return this.updateById(device);
    }

    /**
     * 更新设备报警状态
     * 
     * @param deviceId 设备ID
     * @param alarmStatus 报警状态
     * @return 是否成功
     */
    public boolean updateAlarmStatus(Long deviceId, Integer alarmStatus) {
        LambdaQueryWrapper<PumpDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PumpDevice::getId, deviceId)
                .eq(PumpDevice::getDeleted, 0);
        
        PumpDevice device = this.getOne(wrapper);
        if (device == null) {
            return false;
        }
        
        device.setAlarmStatus(alarmStatus);
        return this.updateById(device);
    }
}