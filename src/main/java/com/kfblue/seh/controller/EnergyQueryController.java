package com.kfblue.seh.controller;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.kfblue.seh.common.Result;
import com.kfblue.seh.constant.DateTypeConsts;
import com.kfblue.seh.service.DeviceDailyStatisticService;
import com.kfblue.seh.service.DeviceReadingService;
import com.kfblue.seh.service.DeviceService;
import com.kfblue.seh.service.RegionService;
import com.kfblue.seh.util.DateUtils;
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
import java.util.Set;


@Tag(name = "能耗查询", description = "能耗查询")
@RestController
@RequestMapping("/energy/query")
@RequiredArgsConstructor
public class EnergyQueryController {
    private final DeviceService deviceService;
    private final DeviceReadingService deviceReadingService;
    private final DeviceDailyStatisticService dailyStatisticService;
    private final RegionService regionService;

    @Operation(summary = "区域信息", description = "区域信息")
    @GetMapping("/regions")
    public Result<List<RegionVO>> regions() {
        return Result.success(regionService.selectByLevel(1));
    }

    @Operation(summary = "当日能耗")
    @GetMapping("/summary")
    public Result<AnalysisSummaryVO> getSummary(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                                @RequestParam("deviceType") String deviceType,
                                                @Parameter(description = "区域", required = true)
                                                @RequestParam("regionId") Long regionId,
                                                @Parameter(description = "日期：yyyy-MM-dd", required = true)
                                                @RequestParam("date") String date) {
        AnalysisSummaryVO result = new AnalysisSummaryVO();
        LocalDate statsDate = LocalDateTimeUtil.parseDate(date, "yyyy-MM-dd");
        Set<Long> regionIds = regionService.selectChildrenIds(regionId);
        result.setTotal(dailyStatisticService.summary(deviceType, statsDate, statsDate, regionIds));
        LocalDate yesterday = statsDate.minusDays(1);
        result.setMomRate(RateUtils.calculateGrowthRate(result.getTotal(), dailyStatisticService.summary(deviceType, yesterday, yesterday, regionIds)));
        LocalDate lastYear = statsDate.minusYears(1);
        result.setYoyRate(RateUtils.calculateGrowthRate(result.getTotal(), dailyStatisticService.summary(deviceType, lastYear, lastYear, regionIds)));
        return Result.success(result);
    }

    @Operation(summary = "小时趋势", description = "小时趋势")
    @GetMapping("/trend/hour")
    public Result<HourCompareVO> trendHour(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                           @RequestParam("deviceType") String deviceType,
                                           @Parameter(description = "区域", required = true)
                                           @RequestParam("regionId") Long regionId,
                                           @Parameter(description = "日期：yyyy-MM-dd", required = true)
                                           @RequestParam("date") String date) {
        LocalDate statsDate = LocalDateTimeUtil.parseDate(date, "yyyy-MM-dd");
        Set<Long> regionIds = regionService.selectChildrenIds(regionId);
        HourCompareVO compare = new HourCompareVO();
        compare.setYesterdays(deviceReadingService.selectHourValues(statsDate.minusDays(1), deviceType, regionIds));
        compare.setTodays(deviceReadingService.selectHourValues(statsDate, deviceType, regionIds));
        return Result.success(compare);
    }

    @Operation(summary = "日趋势", description = "日趋势")
    @GetMapping("/trend/day")
    public Result<DayCompareVO> trendDay(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                         @RequestParam("deviceType") String deviceType,
                                         @Parameter(description = "区域", required = true)
                                         @RequestParam("regionId") Long regionId,
                                         @Parameter(description = "日期：yyyy-MM-dd", required = true)
                                         @RequestParam("date") String date) {
        LocalDate statsDate = LocalDateTimeUtil.parseDate(date, "yyyy-MM-dd");
        LocalDate start = DateUtils.getStartTime(statsDate, DateTypeConsts.MONTH);
        LocalDate end = DateUtils.getEndTime(statsDate, DateTypeConsts.MONTH);
        Set<Long> regionIds = regionService.selectChildrenIds(regionId);
        DayCompareVO compare = new DayCompareVO();
        compare.setCurrents(dailyStatisticService.dayTrend(deviceType, start, end, regionIds));
        compare.setLasts(dailyStatisticService.dayTrend(deviceType, start.minusYears(1), end.minusYears(1), regionIds));
        return Result.success(compare);
    }

    @Operation(summary = "同比分析", description = "同比分析")
    @GetMapping("/yoy")
    public Result<YoyVO> yoy(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                             @RequestParam("deviceType") String deviceType,
                             @Parameter(description = "区域", required = true)
                             @RequestParam("regionId") Long regionId,
                             @Parameter(description = "日期：yyyy-MM-dd", required = true)
                             @RequestParam("date") String date) {
        LocalDate statsDate = LocalDateTimeUtil.parseDate(date, "yyyy-MM-dd");
        LocalDate start = DateUtils.getStartTime(statsDate, DateTypeConsts.MONTH);
        LocalDate end = DateUtils.getEndTime(statsDate, DateTypeConsts.MONTH);
        Set<Long> regionIds = regionService.selectChildrenIds(regionId);
        YoyVO compare = new YoyVO();
        compare.setTodayTotal(deviceReadingService.getDayValue(statsDate, deviceType, regionIds));
        compare.setTodayHourlyUsage(deviceReadingService.getHourValue(LocalDateTime.now(), deviceType));
        compare.setYesterdayTotal(deviceReadingService.getDayValue(statsDate.minusDays(1), deviceType, regionIds));
        compare.setDayRate(RateUtils.calculateGrowthRate(compare.getTodayTotal(), compare.getYesterdayTotal()));
        // 月
        compare.setCurrentMonth(dailyStatisticService.summary(deviceType, start, end, regionIds));
        compare.setLastMonth(dailyStatisticService.summary(deviceType, start.minusMonths(1), end.minusMonths(1), regionIds));
        compare.setMonthRate(RateUtils.calculateGrowthRate(compare.getCurrentMonth(), compare.getLastMonth()));
        // 年
        start = DateUtils.getStartTime(statsDate, DateTypeConsts.YEAR);
        end = DateUtils.getEndTime(statsDate, DateTypeConsts.YEAR);
        compare.setCurrentYear(dailyStatisticService.summary(deviceType, start, end, regionIds));
        compare.setLastYear(dailyStatisticService.summary(deviceType, start.minusYears(1), end.minusYears(1), regionIds));
        compare.setYearRate(RateUtils.calculateGrowthRate(compare.getCurrentYear(), compare.getLastYear()));
        return Result.success(compare);
    }

    @Operation(summary = "区域占比", description = "用量概览数据：支持月度，季度，年度用量概览数据")
    @GetMapping("/distribution")
    public Result<List<DistributionVO>> distribution(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                                     @RequestParam("deviceType") String deviceType,
                                                     @Parameter(description = "区域", required = true)
                                                     @RequestParam("regionId") Long regionId,
                                                     @Parameter(description = "日期：yyyy-MM-dd", required = true)
                                                     @RequestParam("date") String date) {
        Set<Long> regionIds = regionService.selectChildrenIds(regionId);
        regionIds.remove(regionId);
        if (regionIds.isEmpty()) {
            return Result.success();
        }
        LocalDate statsDate = LocalDateTimeUtil.parseDate(date, "yyyy-MM-dd");
        return Result.success(dailyStatisticService.distribution(deviceType, statsDate, statsDate, null, regionIds));
    }


}