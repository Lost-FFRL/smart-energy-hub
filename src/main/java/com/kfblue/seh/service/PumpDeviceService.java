package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.PumpDevice;
import com.kfblue.seh.mapper.PumpDeviceMapper;
import com.kfblue.seh.vo.DeviceStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
     * 获取设备统计信息（VO格式）
     * 
     * @param regionId 区域ID（可选）
     * @return 统计信息
     */
    public DeviceStatisticsVO getDeviceStatistics(Long regionId) {
        LambdaQueryWrapper<PumpDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(regionId != null, PumpDevice::getRegionId, regionId)
               .eq(PumpDevice::getDeleted, 0);
        
        List<PumpDevice> devices = this.list(wrapper);
        
        DeviceStatisticsVO statistics = new DeviceStatisticsVO();
        statistics.setTotalCount((long) devices.size());
        
        long onlineCount = devices.stream().mapToLong(d -> d.getOnlineStatus() != null && d.getOnlineStatus() == 1 ? 1 : 0).sum();
        long workingCount = devices.stream().mapToLong(d -> d.getWorkStatus() != null && d.getWorkStatus() == 1 ? 1 : 0).sum();
        long alarmCount = devices.stream().mapToLong(d -> d.getAlarmStatus() != null && d.getAlarmStatus() == 1 ? 1 : 0).sum();
        
        statistics.setOnlineCount(onlineCount);
        statistics.setWorkingCount(workingCount);
        statistics.setAlarmCount(alarmCount);
        statistics.setOfflineCount(statistics.getTotalCount() - onlineCount);
        
        // 计算比率
        if (statistics.getTotalCount() > 0) {
            statistics.setOnlineRate(onlineCount * 100.0 / statistics.getTotalCount());
            statistics.setOfflineRate((statistics.getTotalCount() - onlineCount) * 100.0 / statistics.getTotalCount());
            statistics.setAlarmRate(alarmCount * 100.0 / statistics.getTotalCount());
        }
        
        return statistics;
    }

    /**
     * 根据设备编码获取设备
     * 
     * @param deviceCode 设备编码
     * @return 设备信息
     */
    public PumpDevice getByDeviceCode(String deviceCode) {
        if (!StringUtils.hasText(deviceCode)) {
            return null;
        }
        
        LambdaQueryWrapper<PumpDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PumpDevice::getDeviceCode, deviceCode)
               .eq(PumpDevice::getDeleted, 0);
        
        return this.getOne(wrapper);
    }

    /**
     * 分页查询设备
     * 
     * @param page 页码
     * @param size 每页大小
     * @param regionId 区域ID
     * @param deviceName 设备名称
     * @return 分页结果
     */
    public IPage<PumpDevice> getDevicesPage(Integer page, Integer size, Long regionId, String deviceName) {
        Page<PumpDevice> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<PumpDevice> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(deviceName), PumpDevice::getDeviceName, deviceName)
               .eq(regionId != null, PumpDevice::getRegionId, regionId)
               .eq(PumpDevice::getDeleted, 0)
               .orderByDesc(PumpDevice::getCreatedAt);
        
        return this.page(pageObj, wrapper);
    }

    /**
     * 根据区域ID查询设备列表
     * 
     * @param regionId 区域ID
     * @return 设备列表
     */
    public List<PumpDevice> getDevicesByRegionId(Long regionId) {
        return getByRegionId(regionId);
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
     * 根据设备编码更新工作状态
     * 
     * @param deviceCode 设备编码
     * @param workStatus 工作状态
     * @return 是否成功
     */
    public boolean updateWorkStatus(String deviceCode, Integer workStatus) {
        PumpDevice device = getByDeviceCode(deviceCode);
        if (device == null) {
            return false;
        }
        return updateWorkStatus(device.getId(), workStatus);
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
     * 根据设备编码更新在线状态
     * 
     * @param deviceCode 设备编码
     * @param onlineStatus 在线状态
     * @return 是否成功
     */
    public boolean updateOnlineStatus(String deviceCode, Integer onlineStatus) {
        PumpDevice device = getByDeviceCode(deviceCode);
        if (device == null) {
            return false;
        }
        return updateOnlineStatus(device.getId(), onlineStatus);
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

    /**
     * 根据设备编码更新报警状态
     * 
     * @param deviceCode 设备编码
     * @param alarmStatus 报警状态
     * @return 是否成功
     */
    public boolean updateAlarmStatus(String deviceCode, Integer alarmStatus) {
        PumpDevice device = getByDeviceCode(deviceCode);
        if (device == null) {
            return false;
        }
        return updateAlarmStatus(device.getId(), alarmStatus);
    }
}