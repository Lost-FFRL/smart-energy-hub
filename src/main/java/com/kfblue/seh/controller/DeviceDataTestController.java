package com.kfblue.seh.controller;

import com.kfblue.seh.common.Result;
import com.kfblue.seh.constants.ApiPaths;
import com.kfblue.seh.entity.DeviceDailyStatistics;
import com.kfblue.seh.scheduler.DeviceDataScheduler;
import com.kfblue.seh.service.DeviceDailyStatisticService;
import com.kfblue.seh.vo.DayValueVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 设备数据测试控制器
 * 用于手动触发定时任务，便于测试
 */
@Tag(name = "设备数据测试", description = "设备数据生成和统计测试接口")
@RestController
@RequestMapping(ApiPaths.API + "/test/device-data")
@RequiredArgsConstructor
public class DeviceDataTestController {

    private final DeviceDataScheduler deviceDataScheduler;
    private final DeviceDailyStatisticService deviceDailyStatisticService;

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
            DeviceDataScheduler.StatisticsCalculationResult result = deviceDataScheduler.manualCalculateStatistics(targetDate);

            String message = String.format(
                    "日统计计算任务执行完成。%s",
                    result.toString()
            );

            return Result.success(message);
        } catch (Exception e) {
            return Result.error("日统计计算任务执行失败: " + e.getMessage());
        }
    }

    @Operation(summary = "生成指定日期范围的设备数据", description = "为所有活跃设备生成指定日期范围内的历史数据")
    @PostMapping("/generate-date-range")
    public Result<String> generateDataForDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // 验证日期范围
            if (start.isAfter(end)) {
                return Result.error("开始日期不能晚于结束日期");
            }

            // 限制日期范围，避免生成过多数据
            long daysBetween = start.until(end.plusDays(1)).getDays();
            if (daysBetween > 365) {
                return Result.error("日期范围不能超过365天");
            }

            deviceDataScheduler.generateDataForDateRange(start, end);
            return Result.success(String.format("指定日期范围数据生成任务执行成功，日期范围: %s 至 %s", startDate, endDate));
        } catch (Exception e) {
            return Result.error("指定日期范围数据生成任务执行失败: " + e.getMessage());
        }
    }

    @Operation(summary = "查询设备日统计数据", description = "根据设备ID和日期范围查询日统计数据")
    @GetMapping("/daily-statistics")
    public Result<List<DeviceDailyStatistics>> getDailyStatistics(
            @Parameter(description = "设备ID", required = true)
            @RequestParam Long deviceId,
            @Parameter(description = "开始日期，格式：yyyy-MM-dd")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期，格式：yyyy-MM-dd")
            @RequestParam(required = false) String endDate) {
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

            if (start.isAfter(end)) {
                return Result.error("开始日期不能晚于结束日期");
            }

            List<DeviceDailyStatistics> statistics = deviceDailyStatisticService.lambdaQuery()
                    .eq(DeviceDailyStatistics::getDeviceId, deviceId)
                    .between(DeviceDailyStatistics::getStatDate, start, end)
                    .orderByDesc(DeviceDailyStatistics::getStatDate)
                    .list();

            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("查询日统计数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "查询日统计汇总数据", description = "根据设备类型和日期范围查询汇总统计数据")
    @GetMapping("/daily-statistics/summary")
    public Result<BigDecimal> getDailyStatisticsSummary(
            @Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表")
            @RequestParam(required = false) String deviceType,
            @Parameter(description = "开始日期，格式：yyyy-MM-dd")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期，格式：yyyy-MM-dd")
            @RequestParam(required = false) String endDate,
            @Parameter(description = "区域ID列表，多个用逗号分隔")
            @RequestParam(required = false) String regionIds) {
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

            if (start.isAfter(end)) {
                return Result.error("开始日期不能晚于结束日期");
            }

            Set<Long> regionIdSet = null;
            if (regionIds != null && !regionIds.trim().isEmpty()) {
                regionIdSet = Set.of(regionIds.split(",")).stream()
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(java.util.stream.Collectors.toSet());
            }

            BigDecimal summary = deviceDailyStatisticService.summary(deviceType, start, end, regionIdSet);
            return Result.success(summary);
        } catch (Exception e) {
            return Result.error("查询日统计汇总数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "查询日统计趋势数据", description = "根据设备类型和日期范围查询日趋势数据")
    @GetMapping("/daily-statistics/trend")
    public Result<List<DayValueVO>> getDailyStatisticsTrend(
            @Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表")
            @RequestParam(required = false) String deviceType,
            @Parameter(description = "开始日期，格式：yyyy-MM-dd")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期，格式：yyyy-MM-dd")
            @RequestParam(required = false) String endDate,
            @Parameter(description = "区域ID列表，多个用逗号分隔")
            @RequestParam(required = false) String regionIds) {
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

            if (start.isAfter(end)) {
                return Result.error("开始日期不能晚于结束日期");
            }

            Set<Long> regionIdSet = null;
            if (regionIds != null && !regionIds.trim().isEmpty()) {
                regionIdSet = Set.of(regionIds.split(",")).stream()
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(java.util.stream.Collectors.toSet());
            }

            List<DayValueVO> trend = deviceDailyStatisticService.dayTrend(deviceType, start, end, regionIdSet);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error("查询日统计趋势数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量计算日期范围的按天统计数据", description = "为指定日期范围内的每一天计算并保存日统计数据")
    @PostMapping("/calculate-statistics-range")
    public Result<String> calculateStatisticsForDateRange(
            @Parameter(description = "开始日期，格式：yyyy-MM-dd", required = true)
            @RequestParam String startDate,
            @Parameter(description = "结束日期，格式：yyyy-MM-dd", required = true)
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // 验证日期范围
            if (start.isAfter(end)) {
                return Result.error("开始日期不能晚于结束日期");
            }

            // 限制日期范围，避免计算过多数据
            long daysBetween = start.until(end.plusDays(1)).getDays();
            if (daysBetween > 365) {
                return Result.error("日期范围不能超过365天");
            }

            int totalDays = 0;
            int successDays = 0;
            int failDays = 0;
            int totalDevicesProcessed = 0;
            int totalSuccessfulDevices = 0;
            int totalFailedDevices = 0;

            // 遍历日期范围，为每一天计算统计数据
            LocalDate currentDate = start;
            while (!currentDate.isAfter(end)) {
                totalDays++;
                try {
                    DeviceDataScheduler.StatisticsCalculationResult result = deviceDataScheduler.manualCalculateStatistics(currentDate);
                    successDays++;
                    totalDevicesProcessed += result.getTotalDevices();
                    totalSuccessfulDevices += result.getSuccessCount();
                    totalFailedDevices += result.getFailCount();
                } catch (Exception e) {
                    failDays++;
                    // 记录失败但继续处理下一天
                }
                currentDate = currentDate.plusDays(1);
            }

            String message = String.format(
                    "日期范围按天统计计算完成。总天数: %d, 成功天数: %d, 失败天数: %d, 处理设备总数: %d, 成功设备数: %d, 失败设备数: %d, 日期范围: %s 至 %s",
                    totalDays, successDays, failDays, totalDevicesProcessed, totalSuccessfulDevices, totalFailedDevices, startDate, endDate
            );

            return Result.success(message);
        } catch (Exception e) {
            return Result.error("批量计算日期范围按天统计数据失败: " + e.getMessage());
        }
    }
}