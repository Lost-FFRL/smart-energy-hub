package com.kfblue.seh.controller;

import com.kfblue.seh.common.Result;
import com.kfblue.seh.service.DeviceReadingService;
import com.kfblue.seh.service.DeviceService;
import com.kfblue.seh.service.RegionService;
import com.kfblue.seh.util.RateUtils;
import com.kfblue.seh.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 能耗信息控制器
 * 提供能耗信息相关的API接口
 */
@Tag(name = "能耗信息", description = "能耗信息管理相关接口")
@RestController
@RequestMapping("/api/energy/info")
@RequiredArgsConstructor
public class EnergyInfoController {
    private final DeviceService deviceService;
    private final DeviceReadingService deviceReadingService;
    private final RegionService regionService;

    @Operation(summary = "获取基础信息（左 1）", description = "获取能耗基础统计信息（按设备类型分类统计）")
    @GetMapping("/basic")
    public Result<List<DeviceTypeStatVO>> getBasicInfo() {
        return Result.success(deviceService.countByDeviceType());
    }

    @Operation(summary = "获取设备在线情况(左 2)", description = "获取当日设备在线状态统计")
    @GetMapping("/device/online")
    public Result<DeviceOnlineStatVO> getDeviceOnlineStatus() {
        DeviceOnlineStatVO result = deviceService.getOnlineStats();
        result.setOnlineRate(result.getTotalCnt() > 0
                ? BigDecimal.valueOf(result.getOnlineCnt()).divide(BigDecimal.valueOf(result.getTotalCnt()), 4, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        return Result.success(result);
    }

    @Operation(summary = "实时用量（中间 1）", description = "根据设备类型获取实时用量，包含今日和昨日24小时数据对比")
    @GetMapping("/hourly")
    public Result<HourlyTrendVO> getHourlyRainfallComparison(
            @Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
            @RequestParam("deviceType") String deviceType) {
        HourlyTrendVO vo = new HourlyTrendVO();
        LocalDateTime now = LocalDateTime.now();
        vo.setTodays(deviceReadingService.selectHourValues(now.toLocalDate(), deviceType, null));
        vo.setYesterdays(deviceReadingService.selectHourValues(now.toLocalDate().minusDays(1), deviceType, null));
        vo.setCurrentHour(deviceReadingService.getHourValue(now, deviceType));
        BigDecimal previous = deviceReadingService.getHourValue(now.minusHours(1), deviceType);
        BigDecimal yesterday = deviceReadingService.getHourValue(now.minusDays(1), deviceType);
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
        Set<Long> regionIds = regionService.selectChildrenIds(regionId);
        vo.setCurrent(deviceReadingService.getDayValue(now.toLocalDate(), deviceType, regionIds));
        vo.setTrends(deviceReadingService.dayStats(now.toLocalDate().minusDays(7), now.toLocalDate(), deviceType, regionIds));
        BigDecimal previous = deviceReadingService.getDayValue(now.toLocalDate().minusDays(1), deviceType, regionIds);
        BigDecimal year = deviceReadingService.getDayValue(now.toLocalDate().minusYears(1), deviceType, regionIds);
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