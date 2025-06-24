package com.kfblue.seh.controller;

import cn.hutool.core.bean.BeanUtil;
import com.kfblue.seh.common.Result;
import com.kfblue.seh.entity.FlowMonitor;
import com.kfblue.seh.entity.LevelMonitor;
import com.kfblue.seh.entity.PumpDevice;
import com.kfblue.seh.service.FlowMonitorService;
import com.kfblue.seh.service.LevelMonitorService;
import com.kfblue.seh.service.PumpDeviceService;
import com.kfblue.seh.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "设备监控-泵房自动化监控", description = "泵房自动化监控相关接口")
@RestController
@RequestMapping("/api/device/monitor")
@RequiredArgsConstructor
public class PumpMonitorController {

    private final PumpDeviceService pumpDeviceService;
    private final FlowMonitorService flowMonitorService;
    private final LevelMonitorService levelMonitorService;

    @Operation(summary = "获取所有设备列表", description = "获取所有水泵设备的基本信息和状态")
    @GetMapping("/pumps")
    public Result<List<PumpSimpleVO>> getAllPumps() {
        // 获取所有水泵设备
        List<PumpDevice> pumpDevices = pumpDeviceService.list();
        return Result.success(BeanUtil.copyToList(pumpDevices, PumpSimpleVO.class));
    }

    @Operation(summary = "查询设备状态", description = "根据水泵ID查询设备状态信息")
    @GetMapping("/pumps/status/{deviceId}")
    public Result<PumpStatusVO> getPumpStatus(@Parameter(description = "水泵ID") @PathVariable Long deviceId) {

        // 获取水泵设备信息
        PumpDevice pumpDevice = pumpDeviceService.getById(deviceId);
        if (pumpDevice == null) {
            return Result.error("水泵设备不存在");
        }

        // 获取水泵状态
        PumpStatusVO pumpStatus = buildPumpStatusVO(pumpDevice);
        return Result.success(pumpStatus);
    }

    @Operation(summary = "流量监控", description = "根据水泵ID查询流量监控数据")
    @GetMapping("/pumps/flow/{deviceId}")
    public Result<List<SimpleFlowDataVO>> getPumpFlowMonitor(
            @Parameter(description = "水泵ID") @PathVariable Long deviceId) {

        List<SimpleFlowDataVO> flowData = buildSimpleFlowData(deviceId);
        if (!flowData.isEmpty()) {
            return Result.success(flowData);
        } else {
            return Result.error("设备不存在或无流量数据");
        }
    }

    @Operation(summary = "液位检测", description = "查询所有设备的当前液位数据")
    @GetMapping("/pumps/level")
    public Result<List<DeviceLevelVO>> getAllPumpsLevelMonitor() {
        // 获取所有水泵设备
        List<PumpDevice> pumpDevices = pumpDeviceService.list();
        if (pumpDevices.isEmpty()) {
            return Result.error("没有找到水泵设备");
        }
        
        // 获取所有设备的当前液位数据
        List<DeviceLevelVO> deviceLevelList = new ArrayList<>();
        
        for (PumpDevice pumpDevice : pumpDevices) {
            DeviceLevelVO deviceLevel = buildDeviceLevelVO(pumpDevice);
            deviceLevelList.add(deviceLevel);
        }
        
        return Result.success(deviceLevelList);
    }

    /**
     * 构建水泵状态VO（单个设备）
     */
    private PumpStatusVO buildPumpStatusVO(PumpDevice pumpDevice) {
        PumpStatusVO vo = new PumpStatusVO();
        if (pumpDevice != null) {
            // 基础信息
            vo.setDeviceId(pumpDevice.getId());
            vo.setDeviceCode(pumpDevice.getDeviceCode());
            vo.setDeviceName(pumpDevice.getDeviceName());

            // 状态信息
            vo.setOnlineStatus(pumpDevice.getOnlineStatus());
            vo.setWorkStatus(pumpDevice.getWorkStatus());
            vo.setAlarmStatus(pumpDevice.getAlarmStatus());

            // 运行参数
            vo.setVoltage(pumpDevice.getVoltage() != null ? pumpDevice.getVoltage() : BigDecimal.ZERO);
            vo.setCurrentA(pumpDevice.getCurrentA());
            vo.setCurrentB(pumpDevice.getCurrentB());
            vo.setCurrentC(pumpDevice.getCurrentC());

            // 压力值
            vo.setPositivePressure(pumpDevice.getPositivePressure());
            vo.setNegativePressure(pumpDevice.getNegativePressure());
        }
        return vo;
    }

    /**
     * 构建简化的流量数据（时间、值、单位）
     */
    private List<SimpleFlowDataVO> buildSimpleFlowData(Long deviceId) {
        // 获取设备信息
        PumpDevice device = pumpDeviceService.getById(deviceId);
        if (device == null) {
            return new ArrayList<>();
        }

        // 获取当前时间倒推24小时的流量统计数据
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(24);
        // 转换为简化的24小时数据点
        return convertHourlyStatsToSimpleFlowData(flowMonitorService.getHourlyFlowStatistics(deviceId, startTime, endTime));
    }

    /**
     * 构建流量监控VO（根据设备ID和时间范围）
     */
    private FlowMonitorVO buildFlowMonitorVO(Long deviceId, Integer hours) {
        FlowMonitorVO vo = new FlowMonitorVO();

        // 获取设备最新流量数据
        FlowMonitor latestFlow = flowMonitorService.getLatestByDeviceId(deviceId);
        if (latestFlow != null) {
            vo.setCurrentFlow(latestFlow.getInstantFlow());
            vo.setTodayTotalFlow(latestFlow.getTotalFlow());
        } else {
            vo.setCurrentFlow(BigDecimal.ZERO);
            vo.setTodayTotalFlow(BigDecimal.ZERO);
        }

        // 获取本月累计流量（示例数据）
        vo.setMonthTotalFlow(BigDecimal.valueOf(11232.0));

        // 获取指定时间范围内的流量趋势数据
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(hours);
        List<FlowMonitor> flowData = flowMonitorService.getByDeviceIdAndTimeRange(deviceId, startTime, endTime);

        // 转换为小时趋势数据
        List<HourlyFlowVO> hourlyTrend = convertToHourlyFlow(flowData, hours);
        vo.setHourlyTrend(hourlyTrend);

        return vo;
    }

    /**
     * 构建设备液位数据VO
     */
    private DeviceLevelVO buildDeviceLevelVO(PumpDevice pumpDevice) {
        DeviceLevelVO vo = new DeviceLevelVO();
        
        // 设置设备基本信息
        vo.setDeviceId(pumpDevice.getId());
        vo.setDeviceCode(pumpDevice.getDeviceCode());
        vo.setDeviceName(pumpDevice.getDeviceName());
        vo.setUnit("m");
        
        // 获取设备最新液位数据
        LevelMonitor latestLevel = levelMonitorService.getLatestByDeviceId(pumpDevice.getId());
        if (latestLevel != null) {
            vo.setCurrentLevel(latestLevel.getCurrentLevel());
        } else {
            // 如果没有液位数据，设置默认值或模拟数据
            vo.setCurrentLevel(BigDecimal.valueOf(2.95)); // 示例液位值
        }
        
        return vo;
    }

    /**
     * 构建液位监控VO（根据设备ID和时间范围）
     */
    private LevelMonitorVO buildLevelMonitorVO(Long deviceId, Integer hours) {
        LevelMonitorVO vo = new LevelMonitorVO();

        // 获取设备最新液位数据
        LevelMonitor latestLevel = levelMonitorService.getLatestByDeviceId(deviceId);
        if (latestLevel != null) {
            vo.setCurrentLevel(latestLevel.getCurrentLevel());
            vo.setLevelPercentage(latestLevel.getLevelPercentage());
            vo.setCurrentCapacity(latestLevel.getCurrentCapacity());
            vo.setTotalCapacity(latestLevel.getTotalCapacity());
            vo.setAlarmStatus(latestLevel.getAlarmStatus());
            vo.setAlarmMessage(latestLevel.getAlarmMessage());
        } else {
            vo.setCurrentLevel(BigDecimal.ZERO);
            vo.setLevelPercentage(BigDecimal.ZERO);
            vo.setCurrentCapacity(BigDecimal.ZERO);
            vo.setTotalCapacity(BigDecimal.valueOf(500.0));
            vo.setAlarmStatus(0);
            vo.setAlarmMessage(null);
        }

        // 获取指定时间范围内的液位趋势数据
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(hours);
        List<LevelMonitor> levelData = levelMonitorService.getByDeviceIdAndTimeRange(deviceId, startTime, endTime);

        // 转换为趋势数据
        List<LevelTrendVO> levelTrend = convertToLevelTrend(levelData);
        vo.setLevelTrend(levelTrend);

        return vo;
    }

    /**
     * 转换流量数据为小时趋势（24小时内每小时一个数据点）
     */
    private List<HourlyFlowVO> convertToHourlyFlow(List<FlowMonitor> flowData, Integer hours) {
        List<HourlyFlowVO> hourlyTrend = new ArrayList<>();

        // 固定返回24小时的数据，每小时一个时间点
        LocalDateTime now = LocalDateTime.now();

        for (int i = 23; i >= 0; i--) {
            LocalDateTime hourTime = now.minusHours(i).withMinute(0).withSecond(0).withNano(0);
            HourlyFlowVO hourlyFlow = new HourlyFlowVO();
            hourlyFlow.setHour(hourTime.format(DateTimeFormatter.ofPattern("HH:mm")));

            // 查找该小时内的流量数据
            LocalDateTime hourStart = hourTime;
            LocalDateTime hourEnd = hourTime.plusHours(1);

            List<FlowMonitor> hourData = flowData.stream()
                    .filter(flow -> flow.getMonitorTime().isAfter(hourStart.minusSeconds(1)) &&
                            flow.getMonitorTime().isBefore(hourEnd))
                    .toList();

            if (!hourData.isEmpty()) {
                // 计算该小时内的平均值、最大值、最小值
                BigDecimal avgFlow = hourData.stream()
                        .map(FlowMonitor::getInstantFlow)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(hourData.size()), 2, BigDecimal.ROUND_HALF_UP);

                BigDecimal maxFlow = hourData.stream()
                        .map(FlowMonitor::getInstantFlow)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

                BigDecimal minFlow = hourData.stream()
                        .map(FlowMonitor::getInstantFlow)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

                // 计算该小时内的流量累计（平均流量 * 1小时）
                BigDecimal hourlyTotalFlow = avgFlow;

                hourlyFlow.setAvgFlow(avgFlow);
                hourlyFlow.setMaxFlow(maxFlow);
                hourlyFlow.setMinFlow(minFlow);
                hourlyFlow.setTotalFlow(hourlyTotalFlow);
            } else {
                // 如果该小时没有数据，生成模拟数据
                double baseFlow = 15 + Math.random() * 10; // 15-25之间的随机值
                hourlyFlow.setAvgFlow(BigDecimal.valueOf(baseFlow).setScale(2, BigDecimal.ROUND_HALF_UP));
                hourlyFlow.setMaxFlow(BigDecimal.valueOf(baseFlow + 2 + Math.random() * 3).setScale(2, BigDecimal.ROUND_HALF_UP));
                hourlyFlow.setMinFlow(BigDecimal.valueOf(baseFlow - 2 - Math.random() * 3).setScale(2, BigDecimal.ROUND_HALF_UP));
                hourlyFlow.setTotalFlow(BigDecimal.valueOf(baseFlow).setScale(2, BigDecimal.ROUND_HALF_UP)); // 该小时的流量值
            }

            hourlyTrend.add(hourlyFlow);
        }

        return hourlyTrend;
    }

    /**
     * 转换小时统计数据为简化的24小时数据点（时间、值、单位）
     */
    private List<SimpleFlowDataVO> convertHourlyStatsToSimpleFlowData(List<SimpleFlowDataVO> hourlyStats) {
        List<SimpleFlowDataVO> dataPoints = new ArrayList<>();

        // 固定返回24小时的数据，每小时一个时间点
        LocalDateTime now = LocalDateTime.now();

        for (int i = 23; i >= 0; i--) {
            LocalDateTime hourTime = now.minusHours(i).withMinute(0).withSecond(0).withNano(0);
            SimpleFlowDataVO point = new SimpleFlowDataVO();
            point.setTime(hourTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            point.setUnit("m³/h");

            // 查找该小时的统计数据
            String targetHour = hourTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));

            SimpleFlowDataVO hourStat = hourlyStats.stream()
                    .filter(stat -> stat.getTime().equals(targetHour))
                    .findFirst()
                    .orElse(null);

            if (hourStat != null && hourStat.getValue() != null) {
                // 使用数据库统计的总流量值
                point.setValue(hourStat.getValue());
            } else {
                // 如果该小时没有数据，生成模拟数据
                double baseFlow = 15 + Math.random() * 10; // 15-25之间的随机值
                point.setValue(BigDecimal.valueOf(baseFlow).setScale(2, RoundingMode.HALF_UP));
            }

            dataPoints.add(point);
        }

        return dataPoints;
    }

    /**
     * 转换液位数据为趋势数据
     */
    private List<LevelTrendVO> convertToLevelTrend(List<LevelMonitor> levelData) {
        List<LevelTrendVO> levelTrend = new ArrayList<>();

        if (levelData.isEmpty()) {
            // 如果没有数据，生成示例数据
            for (int i = 0; i < 24; i++) {
                LevelTrendVO trend = new LevelTrendVO();
                trend.setTime(String.format("%02d:00", i));
                trend.setLevel(BigDecimal.valueOf(3.0 + Math.random() * 0.5));
                trend.setPercentage(BigDecimal.valueOf(60 + Math.random() * 10));
                trend.setCapacity(BigDecimal.valueOf(300 + Math.random() * 50));
                levelTrend.add(trend);
            }
        } else {
            // 处理实际数据
            for (LevelMonitor level : levelData) {
                LevelTrendVO trend = new LevelTrendVO();
                trend.setTime(level.getMonitorTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                trend.setLevel(level.getCurrentLevel());
                trend.setPercentage(level.getLevelPercentage());
                trend.setCapacity(level.getCurrentCapacity());
                levelTrend.add(trend);
            }
        }

        return levelTrend;
    }
}