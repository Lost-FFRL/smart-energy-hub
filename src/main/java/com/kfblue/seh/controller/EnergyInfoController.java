package com.kfblue.seh.controller;

import com.kfblue.seh.common.Result;
import com.kfblue.seh.service.DeviceReadingService;
import com.kfblue.seh.service.DeviceService;
import com.kfblue.seh.util.RateUtils;
import com.kfblue.seh.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 能耗信息控制器
 * 提供能耗信息相关的API接口
 */
@Tag(name = "能耗信息", description = "能耗信息管理相关接口")
@RestController
@RequestMapping("/energy/info")
@RequiredArgsConstructor
public class EnergyInfoController {
    private final DeviceService deviceService;
    private final DeviceReadingService deviceReadingService;

    @Operation(summary = "获取基础信息（左 1）", description = "获取能耗基础统计信息（按设备类型分类统计）")
    @GetMapping("/basic")
    public Result<List<DeviceTypeStatVO>> getBasicInfo() {
        List<Map<String, Integer>> deviceTypeStats = deviceService.countByDeviceType();
        List<DeviceTypeStatVO> voList = deviceTypeStats.stream()
                .map(map -> new DeviceTypeStatVO(
                        map.get("device_type") != null ? String.valueOf(map.get("device_type")) : null,
                        map.get("count")
                ))
                .toList();
        return Result.success(voList);
    }

    @Operation(summary = "获取设备在线情况(左 2)", description = "获取当日设备在线状态统计")
    @GetMapping("/device/online")
    public Result<DeviceOnlineStatVO> getDeviceOnlineStatus() {
        List<Map<String, Integer>> stats = deviceService.getOnlineStats();
        int onlineCnt = 0;
        int offlineCnt = 0;
        int totalCnt = 0;
        for (Map<String, Integer> stat : stats) {
            Integer status = stat.get("online_status");
            Integer count = stat.get("count");
            if (status != null && count != null) {
                if (status == 1) {
                    onlineCnt = count;
                } else {
                    offlineCnt += count;
                }
                totalCnt += count;
            }
        }
        String onlineRate = totalCnt > 0 ? String.format("%.1f%%", onlineCnt * 100.0 / totalCnt) : "0.0%";
        DeviceOnlineStatVO vo = new DeviceOnlineStatVO();
        vo.setOnlineCnt(onlineCnt);
        vo.setOfflineCnt(offlineCnt);
        vo.setTotalCnt(totalCnt);
        vo.setOnlineRate(onlineRate);
        return Result.success(vo);
    }

    @Operation(summary = "实时用量（中间 1）", description = "根据设备类型获取实时用量，包含今日和昨日24小时数据对比")
    @GetMapping("/hourly")
    public Result<HourlyRainfallTrendVO> getHourlyRainfallComparison(
            @Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
            @RequestParam("deviceType") String deviceType) {
        HourlyRainfallTrendVO vo = new HourlyRainfallTrendVO();
        LocalDateTime now = LocalDateTime.now();
        vo.setTodays(deviceReadingService.selectHourValues(now.toLocalDate(), deviceType));
        vo.setYesterdays(deviceReadingService.selectHourValues(now.toLocalDate().minusDays(1), deviceType));
        vo.setCurrentHour(deviceReadingService.getHourValue(now, deviceType));
        double previous = deviceReadingService.getHourValue(now.minusHours(1), deviceType);
        double yesterday = deviceReadingService.getHourValue(now.minusDays(1), deviceType);
        vo.setMomRate(RateUtils.calculateGrowthRate(vo.getCurrentHour(), previous));
        vo.setYoyRate(RateUtils.calculateGrowthRate(vo.getCurrentHour(), yesterday));
        return Result.success(vo);
    }

    @Operation(summary = "日用量（中间 2、3）", description = "区域(可选)")
    @GetMapping("/day")
    public Result<DayTrendVO> getDay(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                     @RequestParam("deviceType") String deviceType,
                                     @Parameter(description = "区域ID")
                                     @RequestParam(value = "regionId", required = false) Long regionId) {
        DayTrendVO vo = new DayTrendVO();
        LocalDateTime now = LocalDateTime.now();
        vo.setCurrent(deviceReadingService.getDayValue(now.toLocalDate(), deviceType, regionId));
        vo.setTrends(deviceReadingService.dayStats(now.toLocalDate().minusDays(7), now.toLocalDate(), deviceType, regionId));
        double previous = deviceReadingService.getDayValue(now.toLocalDate().minusDays(1), deviceType, regionId);
        double year = deviceReadingService.getDayValue(now.toLocalDate().minusYears(1), deviceType, regionId);
        vo.setMomRate(RateUtils.calculateGrowthRate(vo.getCurrent(), previous));
        vo.setYoyRate(RateUtils.calculateGrowthRate(vo.getCurrent(), year));
        return Result.success(vo);
    }

    @Operation(summary = "小时排行榜（右 1）")
    @GetMapping("/hour/rank")
    public Result<List<RankVO>> hourRank(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                         @RequestParam("deviceType") String deviceType,
                                         @Parameter(description = "区域ID")
                                         @RequestParam(value = "regionId", required = false) Long regionId,
                                         @Parameter(description = "取多少名额，默认 10")
                                         @RequestParam(value = "top", required = false, defaultValue = "10") Integer top) {

        return Result.success(deviceReadingService.selectHourRank(deviceType, LocalDateTime.now(), top));
    }

    @Operation(summary = "日排行榜（右 2、3）")
    @GetMapping("/day/rank")
    public Result<List<RankVO>> dayRank(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                      @RequestParam("deviceType") String deviceType,
                                      @Parameter(description = "区域ID")
                                      @RequestParam(value = "regionId", required = false) Long regionId,
                                      @Parameter(description = "取多少名额，默认 10")
                                      @RequestParam(value = "top", required = false, defaultValue = "10") Integer top) {

        return Result.success(deviceReadingService.selectDayRank(deviceType, LocalDate.now(), top));
    }
}