package com.kfblue.seh.scheduler;

import com.kfblue.seh.entity.Device;
import com.kfblue.seh.entity.DeviceReading;
import com.kfblue.seh.service.DeviceDailyStatisticService;
import com.kfblue.seh.service.DeviceDataGeneratorService;
import com.kfblue.seh.service.DeviceReadingService;
import com.kfblue.seh.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备数据定时任务调度器
 * 负责定时生成设备数据和计算日统计
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceDataScheduler {
    
    private final DeviceService deviceService;
    private final DeviceDataGeneratorService deviceDataGeneratorService;
    private final DeviceReadingService deviceReadingService;
    private final DeviceDailyStatisticService deviceDailyStatisticService;
    
    /**
     * 每小时第1分钟生成设备数据
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 1 * * * ?")
    public void generateHourlyData() {
        log.info("开始执行定时生成设备数据任务");
        try {
            manualGenerateData();
            log.info("定时生成设备数据任务执行完成");
        } catch (Exception e) {
            log.error("定时生成设备数据任务执行失败", e);
        }
    }
    
    /**
     * 每日凌晨0点30分计算日统计
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void calculateDailyStatistics() {
        log.info("开始执行定时计算日统计任务");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            manualCalculateStatistics(yesterday);
            log.info("定时计算日统计任务执行完成，日期: {}", yesterday);
        } catch (Exception e) {
            log.error("定时计算日统计任务执行失败", e);
        }
    }
    
    /**
     * 手动生成设备数据
     */
    public void manualGenerateData() {
        log.info("开始手动生成设备数据");
        
        // 获取所有活跃设备
        List<Device> activeDevices = deviceService.getAllActiveDevices();
        log.info("找到 {} 个活跃设备", activeDevices.size());
        
        LocalDateTime now = LocalDateTime.now();
        
        for (Device device : activeDevices) {
            try {
                // 为每个设备生成数据
                DeviceReading reading = deviceDataGeneratorService.generateDeviceReading(device, now);
                if (reading != null) {
                    deviceReadingService.saveReading(reading);
                    log.debug("为设备 {} 生成数据: 当前值={}, 增量值={}", 
                        device.getDeviceCode(), reading.getCurrentValue(), reading.getIncrementValue());
                } else {
                    log.warn("设备 {} 数据生成失败，返回null", device.getDeviceCode());
                }
            } catch (Exception e) {
                log.error("为设备 {} 生成数据失败", device.getDeviceCode(), e);
            }
        }
        
        log.info("手动生成设备数据完成");
    }
    
    /**
     * 手动计算指定日期的日统计
     */
    public void manualCalculateStatistics(LocalDate date) {
        log.info("开始手动计算日统计，日期: {}", date);
        
        // 获取所有活跃设备
        List<Device> activeDevices = deviceService.getAllActiveDevices();
        log.info("找到 {} 个活跃设备需要计算日统计", activeDevices.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (Device device : activeDevices) {
            try {
                deviceDailyStatisticService.calculateAndSaveDailyStatistics(device.getId(), date);
                successCount++;
                log.debug("设备 {} 日统计计算完成", device.getDeviceCode());
            } catch (Exception e) {
                failCount++;
                log.error("设备 {} 日统计计算失败", device.getDeviceCode(), e);
            }
        }
        
        log.info("手动计算日统计完成，日期: {}，成功: {}，失败: {}", date, successCount, failCount);
    }
}