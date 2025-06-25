package com.kfblue.seh.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfblue.seh.entity.LightingDevice;
import com.kfblue.seh.scheduler.LightingDeviceStatusScheduler;
import com.kfblue.seh.service.LightingDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 照明设备控制器
 */
@RestController
@RequestMapping("/api/lighting-devices")
@RequiredArgsConstructor
public class LightingDeviceController {
    
    private final LightingDeviceService lightingDeviceService;
    private final LightingDeviceStatusScheduler statusScheduler;
    
    /**
     * 分页查询照明设备
     */
    @GetMapping
    public ResponseEntity<IPage<LightingDevice>> getDevices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String regionId,
            @RequestParam(required = false) String deviceType) {
        
        Page<LightingDevice> pageParam = new Page<>(page, size);
        IPage<LightingDevice> result;
        
        if (regionId != null && !regionId.isEmpty()) {
            result = lightingDeviceService.getDevicesByRegion(pageParam, regionId);
        } else {
            result = lightingDeviceService.page(pageParam);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据区域ID查询设备
     */
    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<LightingDevice>> getDevicesByRegion(@PathVariable String regionId) {
        List<LightingDevice> devices = lightingDeviceService.getDevicesByRegionId(regionId);
        return ResponseEntity.ok(devices);
    }
    
    /**
     * 获取设备统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getDeviceStatistics() {
        Map<String, Object> statistics = lightingDeviceService.getDeviceStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 根据设备类型获取统计信息
     */
    @GetMapping("/statistics/by-type")
    public ResponseEntity<Map<String, Object>> getStatisticsByDeviceType(
            @RequestParam String deviceType) {
        Map<String, Object> statistics = lightingDeviceService.getStatisticsByDeviceType(deviceType);
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 更新设备工作状态
     */
    @PutMapping("/{deviceCode}/work-status")
    public ResponseEntity<Map<String, Object>> updateWorkStatus(
            @PathVariable String deviceCode,
            @RequestParam int workStatus) {
        
        boolean success = lightingDeviceService.updateWorkStatus(deviceCode, workStatus);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "设备工作状态更新成功" : "设备工作状态更新失败");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 手动触发设备状态模拟
     * 用于测试定时任务功能
     */
    @PostMapping("/simulate-status")
    public ResponseEntity<Map<String, Object>> simulateDeviceStatus() {
        try {
            statusScheduler.simulateDeviceStatus();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "设备状态模拟执行成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "设备状态模拟执行失败: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取设备详情
     */
    @GetMapping("/{deviceCode}")
    public ResponseEntity<LightingDevice> getDeviceByCode(@PathVariable String deviceCode) {
        LightingDevice device = lightingDeviceService.getByDeviceCode(deviceCode);
        if (device != null) {
            return ResponseEntity.ok(device);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}