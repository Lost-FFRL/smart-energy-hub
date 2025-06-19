package com.kfblue.seh.controller;

import com.kfblue.seh.common.Result;
import com.kfblue.seh.constants.DateTypeConsts;
import com.kfblue.seh.service.DeviceDailyStatisticService;
import com.kfblue.seh.service.DeviceReadingService;
import com.kfblue.seh.service.DeviceService;
import com.kfblue.seh.util.DateUtils;
import com.kfblue.seh.util.RateUtils;
import com.kfblue.seh.vo.AnalysisSummaryVO;
import com.kfblue.seh.vo.DistributionVO;
import com.kfblue.seh.vo.MonthValueVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 能耗分析控制器
 * 提供能耗分析相关的API接口
 */
@Tag(name = "能耗分析", description = "能耗分析管理相关接口")
@RestController
@RequestMapping("/api/energy/analysis")
@RequiredArgsConstructor
public class EnergyAnalysisController {
    private final DeviceService deviceService;
    private final DeviceReadingService deviceReadingService;
    private final DeviceDailyStatisticService dailyStatisticService;

    @Operation(summary = "用量概览", description = "用量概览数据：支持月度，季度，年度用量概览数据")
    @GetMapping("/summary")
    public Result<AnalysisSummaryVO> getSummary(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                                @RequestParam("deviceType") String deviceType,
                                                @Parameter(description = "时间维度：month-月度，quarter-季度，year-年度", required = true)
                                                @RequestParam("timeDimension") String timeDimension) {
        AnalysisSummaryVO result = new AnalysisSummaryVO();
        LocalDate date = LocalDate.now();
        LocalDate start = DateUtils.getStartTime(date, timeDimension);
        LocalDate end = DateUtils.getEndTime(date, timeDimension);
        result.setTotal(dailyStatisticService.summary(deviceType, start, end, null));
        // 环比-上一个月度/季度/年度
        start = DateUtils.getStartTime(start.minusDays(1), timeDimension);
        end = DateUtils.getEndTime(start.minusDays(1), timeDimension);
        result.setMomRate(RateUtils.calculateGrowthRate(result.getTotal(), dailyStatisticService.summary(deviceType, start, end, null)));
        start = start.minusYears(1);
        end = end.minusYears(1);
        result.setYoyRate(RateUtils.calculateGrowthRate(result.getTotal(), dailyStatisticService.summary(deviceType, start, end, null)));
        return Result.success(result);
    }

    @Operation(summary = "分布", description = "用量概览数据：支持月度，季度，年度用量概览数据")
    @GetMapping("/distribution")
    public Result<List<DistributionVO>> distribution(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                                     @RequestParam("deviceType") String deviceType,
                                                     @Parameter(description = "时间维度：month-月度，quarter-季度，year-年度", required = true)
                                                     @RequestParam("timeDimension") String timeDimension) {
        LocalDate date = LocalDate.now();
        LocalDate start = DateUtils.getStartTime(date, timeDimension);
        LocalDate end = DateUtils.getEndTime(date, timeDimension);
        return Result.success(dailyStatisticService.distribution(deviceType, start, end, null, null));
    }

    @Operation(summary = "分析趋势", description = "获取用量分析趋势数据")
    @GetMapping("/trend")
    public Result<List<MonthValueVO>> trend(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                            @RequestParam("deviceType") String deviceType) {
        LocalDate date = LocalDate.now();
        LocalDate start = DateUtils.getStartTime(date, DateTypeConsts.YEAR).minusYears(1);
        LocalDate end = DateUtils.getEndTime(date, DateTypeConsts.YEAR);
        return Result.success(dailyStatisticService.monthTrend(deviceType, start, end));
    }

    @Operation(summary = "析排行榜", description = "获取用量分析排行榜数据")
    @GetMapping("/ranking")
    public Result<List<DistributionVO>> getWaterAnalysisRanking(@Parameter(description = "设备类型：water:水表,electric:电表,gas:气表,heat:热表", required = true)
                                                                @RequestParam("deviceType") String deviceType,
                                                                @Parameter(description = "Top N", required = false)
                                                                @RequestParam(value = "top", defaultValue = "10") Integer top) {
        return Result.success(dailyStatisticService.distribution(deviceType, null, null, top, null));
    }

}