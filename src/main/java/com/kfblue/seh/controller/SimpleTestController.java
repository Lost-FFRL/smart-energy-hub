package com.kfblue.seh.controller;

import com.kfblue.seh.common.Result;
import com.kfblue.seh.scheduler.SimpleSchedulerTest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 简化的测试控制器
 * 用于验证定时器和模拟数据生成功能
 */
@Tag(name = "简化测试", description = "定时器和模拟数据测试接口")
@RestController
@RequestMapping("/api/simple-test")
@RequiredArgsConstructor
@Slf4j
public class SimpleTestController {
    
    private final SimpleSchedulerTest simpleSchedulerTest;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now().format(FORMATTER));
        data.put("service", "Smart Energy Hub - Simple Test");
        
        log.info("[健康检查] 服务正常运行");
        return Result.success(data);
    }
    
    @Operation(summary = "手动触发定时器测试", description = "手动执行所有定时器任务")
    @PostMapping("/trigger-scheduler")
    public Result<String> triggerScheduler() {
        try {
            log.info("[手动触发] 开始执行定时器测试");
            simpleSchedulerTest.manualTriggerAll();
            
            String message = String.format("定时器测试执行成功，执行时间: %s", 
                    LocalDateTime.now().format(FORMATTER));
            
            return Result.success(message);
        } catch (Exception e) {
            log.error("[手动触发] 定时器测试执行失败", e);
            return Result.error("定时器测试执行失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取系统状态", description = "获取当前系统运行状态信息")
    @GetMapping("/status")
    public Result<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 系统信息
        status.put("currentTime", LocalDateTime.now().format(FORMATTER));
        status.put("javaVersion", System.getProperty("java.version"));
        status.put("osName", System.getProperty("os.name"));
        
        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("total", formatBytes(totalMemory));
        memoryInfo.put("used", formatBytes(usedMemory));
        memoryInfo.put("free", formatBytes(freeMemory));
        memoryInfo.put("usagePercent", String.format("%.2f%%", (double) usedMemory / totalMemory * 100));
        
        status.put("memory", memoryInfo);
        
        // 定时器状态
        Map<String, Object> schedulerInfo = new HashMap<>();
        schedulerInfo.put("enabled", true);
        schedulerInfo.put("tasks", 3);
        schedulerInfo.put("description", "每分钟、每5分钟、每10分钟定时任务");
        
        status.put("scheduler", schedulerInfo);
        
        log.info("[系统状态] 状态查询成功");
        return Result.success(status);
    }
    
    @Operation(summary = "测试日志输出", description = "测试不同级别的日志输出")
    @PostMapping("/test-logs")
    public Result<String> testLogs(@RequestParam(defaultValue = "测试消息") String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        
        log.debug("[日志测试] DEBUG级别: {} - {}", message, timestamp);
        log.info("[日志测试] INFO级别: {} - {}", message, timestamp);
        log.warn("[日志测试] WARN级别: {} - {}", message, timestamp);
        
        // 不输出ERROR级别，避免误导
        
        return Result.success(String.format("日志测试完成，消息: %s，时间: %s", message, timestamp));
    }
    
    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}