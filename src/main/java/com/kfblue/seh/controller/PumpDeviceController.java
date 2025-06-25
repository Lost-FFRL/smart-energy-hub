package com.kfblue.seh.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfblue.seh.entity.PumpDevice;
import com.kfblue.seh.scheduler.PumpDeviceDataScheduler;
import com.kfblue.seh.service.PumpDeviceService;
import com.kfblue.seh.vo.DeviceStatisticsVO;
import com.kfblue.seh.vo.PageVO;
import com.kfblue.seh.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 水泵设备控制器
 * 
 * @author system
 * @since 2025-01-27
 */
@Tag(name = "水泵设备管理", description = "水泵设备相关接口")
@RestController
@RequestMapping("/api/pump-devices")
@RequiredArgsConstructor
public class PumpDeviceController {

    private final PumpDeviceService pumpDeviceService;
    private final PumpDeviceDataScheduler pumpDeviceDataScheduler;

    /**
     * 分页查询水泵设备
     */
    @Operation(summary = "分页查询水泵设备")
    @GetMapping
    public Result<PageVO<PumpDevice>> getDevices(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "区域ID") @RequestParam(required = false) Long regionId,
            @Parameter(description = "设备名称") @RequestParam(required = false) String deviceName) {
        
        IPage<PumpDevice> devicePage = pumpDeviceService.getDevicesPage(page, size, regionId, deviceName);
        
        PageVO<PumpDevice> pageVO = new PageVO<>();
        pageVO.setRecords(devicePage.getRecords());
        pageVO.setTotal(devicePage.getTotal());
        pageVO.setCurrent(devicePage.getCurrent());
        pageVO.setSize(devicePage.getSize());
        pageVO.setPages(devicePage.getPages());
        
        return Result.success(pageVO);
    }

    /**
     * 根据区域ID查询设备
     */
    @Operation(summary = "根据区域ID查询设备")
    @GetMapping("/region/{regionId}")
    public Result<List<PumpDevice>> getDevicesByRegion(
            @Parameter(description = "区域ID") @PathVariable Long regionId) {
        List<PumpDevice> devices = pumpDeviceService.getDevicesByRegionId(regionId);
        return Result.success(devices);
    }

    /**
     * 获取设备统计信息
     */
    @Operation(summary = "获取设备统计信息")
    @GetMapping("/statistics")
    public Result<DeviceStatisticsVO> getDeviceStatistics(
            @Parameter(description = "区域ID") @RequestParam(required = false) Long regionId) {
        DeviceStatisticsVO statistics = pumpDeviceService.getDeviceStatistics(regionId);
        return Result.success(statistics);
    }

    /**
     * 根据设备编码获取设备详情
     */
    @Operation(summary = "根据设备编码获取设备详情")
    @GetMapping("/code/{deviceCode}")
    public Result<PumpDevice> getDeviceByCode(
            @Parameter(description = "设备编码") @PathVariable String deviceCode) {
        PumpDevice device = pumpDeviceService.getByDeviceCode(deviceCode);
        if (device == null) {
            return Result.error("设备不存在");
        }
        return Result.success(device);
    }

    /**
     * 更新设备工作状态
     */
    @Operation(summary = "更新设备工作状态")
    @PutMapping("/{deviceCode}/work-status")
    public Result<Void> updateWorkStatus(
            @Parameter(description = "设备编码") @PathVariable String deviceCode,
            @Parameter(description = "工作状态") @RequestParam Integer workStatus) {
        boolean success = pumpDeviceService.updateWorkStatus(deviceCode, workStatus);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 更新设备在线状态
     */
    @Operation(summary = "更新设备在线状态")
    @PutMapping("/{deviceCode}/online-status")
    public Result<Void> updateOnlineStatus(
            @Parameter(description = "设备编码") @PathVariable String deviceCode,
            @Parameter(description = "在线状态") @RequestParam Integer onlineStatus) {
        boolean success = pumpDeviceService.updateOnlineStatus(deviceCode, onlineStatus);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 更新设备报警状态
     */
    @Operation(summary = "更新设备报警状态")
    @PutMapping("/{deviceCode}/alarm-status")
    public Result<Void> updateAlarmStatus(
            @Parameter(description = "设备编码") @PathVariable String deviceCode,
            @Parameter(description = "报警状态") @RequestParam Integer alarmStatus) {
        boolean success = pumpDeviceService.updateAlarmStatus(deviceCode, alarmStatus);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 手动触发水泵设备数据生成（测试用）
     */
    @Operation(summary = "手动触发水泵设备数据生成", description = "手动触发定时任务，生成水泵设备数据和流水表数据")
    @PostMapping("/manual-generate-data")
    public Result<String> manualGenerateData() {
        try {
            pumpDeviceDataScheduler.manualGenerateData();
            return Result.success("水泵设备数据生成任务已触发");
        } catch (Exception e) {
            return Result.error("触发数据生成任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取设备详情
     */
    @Operation(summary = "获取设备详情")
    @GetMapping("/{id}")
    public Result<PumpDevice> getDeviceById(
            @Parameter(description = "设备ID") @PathVariable Long id) {
        PumpDevice device = pumpDeviceService.getById(id);
        if (device == null) {
            return Result.error("设备不存在");
        }
        return Result.success(device);
    }

    /**
     * 创建设备
     */
    @Operation(summary = "创建设备")
    @PostMapping
    public Result<PumpDevice> createDevice(@RequestBody PumpDevice device) {
        boolean success = pumpDeviceService.save(device);
        return success ? Result.success(device) : Result.error("创建失败");
    }

    /**
     * 更新设备
     */
    @Operation(summary = "更新设备")
    @PutMapping("/{id}")
    public Result<PumpDevice> updateDevice(
            @Parameter(description = "设备ID") @PathVariable Long id,
            @RequestBody PumpDevice device) {
        device.setId(id);
        boolean success = pumpDeviceService.updateById(device);
        return success ? Result.success(device) : Result.error("更新失败");
    }

    /**
     * 删除设备
     */
    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public Result<Void> deleteDevice(
            @Parameter(description = "设备ID") @PathVariable Long id) {
        boolean success = pumpDeviceService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}