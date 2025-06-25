package com.kfblue.seh.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kfblue.seh.entity.LightingDevice;
import com.kfblue.seh.service.LightingDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 照明设备状态模拟定时任务
 * 每10分钟执行一次，模拟设备状态变化
 * 确保离线率不超过10%，告警率不超过10%
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LightingDeviceStatusScheduler {
    
    private final LightingDeviceService lightingDeviceService;
    private final Random random = new Random();
    
    // 最大离线率 10%
    private static final double MAX_OFFLINE_RATE = 0.10;
    // 最大告警率 10%
    private static final double MAX_ALARM_RATE = 0.10;
    
    /**
     * 每10分钟执行一次设备状态模拟
     * cron表达式: 每10分钟执行一次
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void simulateDeviceStatusChanges() {
        log.info("开始执行照明设备状态模拟任务");
        try {
            simulateDeviceStatus();
            log.info("照明设备状态模拟任务执行完成");
        } catch (Exception e) {
            log.error("照明设备状态模拟任务执行失败", e);
        }
    }
    
    /**
     * 模拟设备状态变化
     */
    public void simulateDeviceStatus() {
        // 获取所有照明设备
        LambdaQueryWrapper<LightingDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LightingDevice::getDeleted, 0);
        List<LightingDevice> devices = lightingDeviceService.list(wrapper);
        
        if (devices.isEmpty()) {
            log.warn("没有找到照明设备，跳过状态模拟");
            return;
        }
        
        log.info("找到 {} 个照明设备，开始模拟状态变化", devices.size());
        
        // 计算当前状态统计
        DeviceStatusStats currentStats = calculateCurrentStats(devices);
        log.info("当前设备状态 - 总数: {}, 离线: {}, 告警: {}, 离线率: {:.2f}%, 告警率: {:.2f}%", 
            currentStats.total, currentStats.offline, currentStats.alarm, 
            currentStats.offlineRate * 100, currentStats.alarmRate * 100);
        
        // 模拟状态变化
        int statusChanges = 0;
        for (LightingDevice device : devices) {
            boolean changed = false;
            
            // 模拟在线状态变化
            if (shouldChangeOnlineStatus(device, currentStats)) {
                int newOnlineStatus = generateNewOnlineStatus(device, currentStats);
                if (newOnlineStatus != device.getOnlineStatus()) {
                    device.setOnlineStatus(newOnlineStatus);
                    changed = true;
                    // 更新统计
                    updateStatsForOnlineChange(currentStats, device.getOnlineStatus(), newOnlineStatus);
                }
            }
            
            // 模拟告警状态变化
            if (shouldChangeAlarmStatus(device, currentStats)) {
                int newAlarmStatus = generateNewAlarmStatus(device, currentStats);
                if (newAlarmStatus != device.getAlarmStatus()) {
                    device.setAlarmStatus(newAlarmStatus);
                    changed = true;
                    // 更新统计
                    updateStatsForAlarmChange(currentStats, device.getAlarmStatus(), newAlarmStatus);
                }
            }
            
            // 模拟工作状态变化（只有在线设备才能改变工作状态）
            if (device.getOnlineStatus() == 1 && shouldChangeWorkStatus(device)) {
                int newWorkStatus = random.nextBoolean() ? 1 : 0;
                if (newWorkStatus != device.getWorkStatus()) {
                    device.setWorkStatus(newWorkStatus);
                    changed = true;
                }
            }
            
            // 保存变化
            if (changed) {
                lightingDeviceService.updateById(device);
                statusChanges++;
                log.debug("设备 {} 状态更新 - 在线: {}, 工作: {}, 告警: {}", 
                    device.getDeviceCode(), device.getOnlineStatus(), 
                    device.getWorkStatus(), device.getAlarmStatus());
            }
        }
        
        log.info("设备状态模拟完成，共更新 {} 个设备状态", statusChanges);
        
        // 记录最终状态
        DeviceStatusStats finalStats = calculateCurrentStats(devices);
        log.info("最终设备状态 - 总数: {}, 离线: {}, 告警: {}, 离线率: {:.2f}%, 告警率: {:.2f}%", 
            finalStats.total, finalStats.offline, finalStats.alarm, 
            finalStats.offlineRate * 100, finalStats.alarmRate * 100);
    }
    
    /**
     * 计算当前设备状态统计
     */
    private DeviceStatusStats calculateCurrentStats(List<LightingDevice> devices) {
        int total = devices.size();
        int offline = 0;
        int alarm = 0;
        
        for (LightingDevice device : devices) {
            if (device.getOnlineStatus() == null || device.getOnlineStatus() == 0) {
                offline++;
            }
            if (device.getAlarmStatus() != null && device.getAlarmStatus() == 1) {
                alarm++;
            }
        }
        
        return new DeviceStatusStats(total, offline, alarm);
    }
    
    /**
     * 判断是否应该改变在线状态
     */
    private boolean shouldChangeOnlineStatus(LightingDevice device, DeviceStatusStats stats) {
        // 20% 的概率改变在线状态
        return random.nextDouble() < 0.20;
    }
    
    /**
     * 生成新的在线状态
     */
    private int generateNewOnlineStatus(LightingDevice device, DeviceStatusStats stats) {
        int currentStatus = device.getOnlineStatus() == null ? 0 : device.getOnlineStatus();
        
        // 如果当前离线率已经达到上限，优先让设备上线
        if (stats.offlineRate >= MAX_OFFLINE_RATE && currentStatus == 0) {
            return 1; // 强制上线
        }
        
        // 如果当前离线率很低，可以让一些设备离线
        if (stats.offlineRate < MAX_OFFLINE_RATE * 0.5 && currentStatus == 1) {
            return random.nextBoolean() ? 0 : 1;
        }
        
        // 默认倾向于保持在线
        return random.nextDouble() < 0.8 ? 1 : 0;
    }
    
    /**
     * 判断是否应该改变告警状态
     */
    private boolean shouldChangeAlarmStatus(LightingDevice device, DeviceStatusStats stats) {
        // 15% 的概率改变告警状态
        return random.nextDouble() < 0.15;
    }
    
    /**
     * 生成新的告警状态
     */
    private int generateNewAlarmStatus(LightingDevice device, DeviceStatusStats stats) {
        int currentStatus = device.getAlarmStatus() == null ? 0 : device.getAlarmStatus();
        
        // 如果当前告警率已经达到上限，优先清除告警
        if (stats.alarmRate >= MAX_ALARM_RATE && currentStatus == 1) {
            return 0; // 强制清除告警
        }
        
        // 如果当前告警率很低，可以产生一些告警
        if (stats.alarmRate < MAX_ALARM_RATE * 0.5 && currentStatus == 0) {
            return random.nextBoolean() ? 1 : 0;
        }
        
        // 默认倾向于正常状态
        return random.nextDouble() < 0.9 ? 0 : 1;
    }
    
    /**
     * 判断是否应该改变工作状态
     */
    private boolean shouldChangeWorkStatus(LightingDevice device) {
        // 30% 的概率改变工作状态
        return random.nextDouble() < 0.30;
    }
    
    /**
     * 更新在线状态变化的统计
     */
    private void updateStatsForOnlineChange(DeviceStatusStats stats, Integer oldStatus, int newStatus) {
        if ((oldStatus == null || oldStatus == 0) && newStatus == 1) {
            // 从离线变为在线
            stats.offline--;
        } else if ((oldStatus != null && oldStatus == 1) && newStatus == 0) {
            // 从在线变为离线
            stats.offline++;
        }
        stats.updateRates();
    }
    
    /**
     * 更新告警状态变化的统计
     */
    private void updateStatsForAlarmChange(DeviceStatusStats stats, Integer oldStatus, int newStatus) {
        if ((oldStatus == null || oldStatus == 0) && newStatus == 1) {
            // 从正常变为告警
            stats.alarm++;
        } else if ((oldStatus != null && oldStatus == 1) && newStatus == 0) {
            // 从告警变为正常
            stats.alarm--;
        }
        stats.updateRates();
    }
    
    /**
     * 设备状态统计类
     */
    private static class DeviceStatusStats {
        int total;
        int offline;
        int alarm;
        double offlineRate;
        double alarmRate;
        
        public DeviceStatusStats(int total, int offline, int alarm) {
            this.total = total;
            this.offline = offline;
            this.alarm = alarm;
            updateRates();
        }
        
        public void updateRates() {
            this.offlineRate = total > 0 ? (double) offline / total : 0;
            this.alarmRate = total > 0 ? (double) alarm / total : 0;
        }
    }
}