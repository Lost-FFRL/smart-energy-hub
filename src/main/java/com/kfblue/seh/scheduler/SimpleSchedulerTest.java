package com.kfblue.seh.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 简化的定时器测试类
 * 用于验证定时器功能和生成模拟数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleSchedulerTest {
    
    private final Random random = new Random();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 每分钟执行一次 - 用于快速验证定时器功能
     */
    @Scheduled(cron = "0 * * * * ?")
    public void everyMinuteTask() {
        String currentTime = LocalDateTime.now().format(FORMATTER);
        log.info("[每分钟定时器] 执行时间: {}", currentTime);
        
        // 生成简单的模拟数据
        generateSimpleData();
    }
    
    /**
     * 每5分钟执行一次 - 模拟设备状态变化
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void everyFiveMinutesTask() {
        String currentTime = LocalDateTime.now().format(FORMATTER);
        log.info("[每5分钟定时器] 执行时间: {}", currentTime);
        
        // 模拟设备状态变化
        simulateDeviceStatus();
    }
    
    /**
     * 每10分钟执行一次 - 模拟数据统计
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void everyTenMinutesTask() {
        String currentTime = LocalDateTime.now().format(FORMATTER);
        log.info("[每10分钟定时器] 执行时间: {}", currentTime);
        
        // 模拟数据统计
        simulateDataStatistics();
    }
    
    /**
     * 生成简单的模拟数据
     */
    private void generateSimpleData() {
        try {
            // 模拟温度数据
            double temperature = 20 + random.nextDouble() * 10; // 20-30度
            
            // 模拟湿度数据
            double humidity = 40 + random.nextDouble() * 40; // 40-80%
            
            // 模拟电量数据
            double power = 100 + random.nextDouble() * 500; // 100-600W
            
            log.info("[模拟数据生成] 温度: {}°C, 湿度: {}%, 功率: {}W", 
                    String.format("%.2f", temperature), 
                    String.format("%.2f", humidity), 
                    String.format("%.2f", power));
                    
        } catch (Exception e) {
            log.error("[模拟数据生成] 执行失败", e);
        }
    }
    
    /**
     * 模拟设备状态变化
     */
    private void simulateDeviceStatus() {
        try {
            // 模拟设备在线状态
            boolean isOnline = random.nextDouble() > 0.1; // 90%在线率
            
            // 模拟设备工作状态
            boolean isWorking = isOnline && random.nextDouble() > 0.2; // 在线设备80%工作
            
            // 模拟设备告警状态
            boolean hasAlarm = random.nextDouble() < 0.05; // 5%告警率
            
            log.info("[设备状态模拟] 在线: {}, 工作: {}, 告警: {}", 
                    isOnline ? "是" : "否", 
                    isWorking ? "是" : "否", 
                    hasAlarm ? "是" : "否");
                    
        } catch (Exception e) {
            log.error("[设备状态模拟] 执行失败", e);
        }
    }
    
    /**
     * 模拟数据统计
     */
    private void simulateDataStatistics() {
        try {
            // 模拟设备总数
            int totalDevices = 50 + random.nextInt(50); // 50-100台设备
            
            // 模拟在线设备数
            int onlineDevices = (int) (totalDevices * (0.85 + random.nextDouble() * 0.1)); // 85-95%在线
            
            // 模拟告警设备数
            int alarmDevices = (int) (totalDevices * random.nextDouble() * 0.1); // 0-10%告警
            
            // 模拟数据生成量
            int dataCount = onlineDevices * (8 + random.nextInt(5)); // 每台在线设备8-12条数据
            
            log.info("[数据统计模拟] 设备总数: {}, 在线: {}, 告警: {}, 数据量: {}", 
                    totalDevices, onlineDevices, alarmDevices, dataCount);
                    
        } catch (Exception e) {
            log.error("[数据统计模拟] 执行失败", e);
        }
    }
    
    /**
     * 手动触发所有模拟任务 - 用于测试
     */
    public void manualTriggerAll() {
        log.info("[手动触发] 开始执行所有模拟任务");
        
        generateSimpleData();
        simulateDeviceStatus();
        simulateDataStatistics();
        
        log.info("[手动触发] 所有模拟任务执行完成");
    }
}