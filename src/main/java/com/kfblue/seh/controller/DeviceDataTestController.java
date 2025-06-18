package com.kfblue.seh.controller;

import com.kfblue.seh.common.Result;
import com.kfblue.seh.scheduler.DeviceDataScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 设备数据测试控制器
 * 用于手动触发定时任务，便于测试
 */
@Tag(name = "设备数据测试", description = "设备数据生成和统计测试接口")
@RestController
@RequestMapping("/api/test/device-data")
@RequiredArgsConstructor
public class DeviceDataTestController {
    
    private final DeviceDataScheduler deviceDataScheduler;
    
    @Operation(summary = "手动生成设备数据", description = "手动触发设备数据生成任务")
    @PostMapping("/generate")
    public Result<String> generateData() {
        try {
            deviceDataScheduler.manualGenerateData();
            return Result.success("设备数据生成任务执行成功");
        } catch (Exception e) {
            return Result.error("设备数据生成任务执行失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "手动计算日统计", description = "手动触发指定日期的日统计计算任务")
    @PostMapping("/calculate-statistics")
    public Result<String> calculateStatistics(@RequestParam(required = false) String date) {
        try {
            LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now().minusDays(1);
            deviceDataScheduler.manualCalculateStatistics(targetDate);
            return Result.success("日统计计算任务执行成功，日期: " + targetDate);
        } catch (Exception e) {
            return Result.error("日统计计算任务执行失败: " + e.getMessage());
        }
    }
}