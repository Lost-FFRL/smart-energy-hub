package com.kfblue.seh.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfblue.seh.common.Result;
import com.kfblue.seh.constants.ApiPaths;

import com.kfblue.seh.entity.Device;
import com.kfblue.seh.service.DeviceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备控制器
 *
 * @author system
 * @since 2025-06-17
 */
@RestController
@Tag(name = "设备管理")
@RequestMapping(ApiPaths.API_V0 + "/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * 分页查询设备列表
     *
     * @param current      当前页
     * @param size         页大小
     * @param regionId     区域ID
     * @param deviceType   设备类型
     * @param onlineStatus 在线状态
     * @param keyword      关键词
     * @return 设备分页数据
     */
    @GetMapping
    public Result<Page<Device>> getDevices(@RequestParam(defaultValue = "1") Long current,
                                           @RequestParam(defaultValue = "10") Long size,
                                           @RequestParam(required = false) Long regionId,
                                           @RequestParam(required = false) String deviceType,
                                           @RequestParam(required = false) Integer onlineStatus,
                                           @RequestParam(required = false) String keyword) {

        Page<Device> page = new Page<>(current, size);
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();

        if (regionId != null) {
            queryWrapper.eq(Device::getRegionId, regionId);
        }
        if (deviceType != null && !deviceType.isEmpty()) {
            queryWrapper.eq(Device::getDeviceType, deviceType);
        }
        if (onlineStatus != null) {
            queryWrapper.eq(Device::getOnlineStatus, onlineStatus);
        }
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Device::getDeviceCode, keyword)
                    .or().like(Device::getDeviceName, keyword));
        }

        queryWrapper.eq(Device::getDeleted, 0)
                .orderByDesc(Device::getCreatedAt);

        Page<Device> result = deviceService.page(page, queryWrapper);
        return Result.success(result);
    }

    /**
     * 根据设备编码查询设备
     *
     * @param deviceCode 设备编码
     * @return 设备信息
     */
    @GetMapping("/code/{deviceCode}")
    public Result<Device> getByDeviceCode(@PathVariable String deviceCode) {
        Device device = deviceService.getByDeviceCode(deviceCode);
        if (device != null) {
            return Result.success(device);
        } else {
            return Result.error("设备不存在");
        }
    }

    /**
     * 根据区域ID查询设备列表
     *
     * @param regionId 区域ID
     * @return 设备列表
     */
    @GetMapping("/region/{regionId}")
    public Result<List<Device>> getByRegionId(@PathVariable Long regionId) {
        List<Device> devices = deviceService.getByRegionId(regionId);
        return Result.success(devices);
    }

    /**
     * 根据设备类型查询设备列表
     *
     * @param deviceType 设备类型
     * @return 设备列表
     */
    @GetMapping("/type/{deviceType}")
    public Result<List<Device>> getByDeviceType(@PathVariable String deviceType) {
        List<Device> devices = deviceService.getByDeviceType(deviceType);
        return Result.success(devices);
    }

    /**
     * 根据在线状态查询设备列表
     *
     * @param onlineStatus 在线状态
     * @return 设备列表
     */
    @GetMapping("/online-status/{onlineStatus}")
    public Result<List<Device>> getByOnlineStatus(@PathVariable Integer onlineStatus) {
        List<Device> devices = deviceService.getByOnlineStatus(onlineStatus);
        return Result.success(devices);
    }


    /**
     * 根据ID查询设备详情
     *
     * @param id 设备ID
     * @return 设备信息
     */
    @GetMapping("/{id}")
    public Result<Device> getById(@PathVariable Long id) {
        Device device = deviceService.getById(id);
        if (device != null) {
            return Result.success(device);
        } else {
            return Result.error("设备不存在");
        }
    }

    /**
     * 创建设备
     *
     * @param device 设备信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createDevice(@RequestBody Device device) {
        try {
            boolean success = deviceService.createDevice(device);
            if (success) {
                return Result.success("设备创建成功");
            } else {
                return Result.error("设备创建失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新设备
     *
     * @param id     设备ID
     * @param device 设备信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        try {
            device.setId(id);
            boolean success = deviceService.updateDevice(device);
            if (success) {
                return Result.success("设备更新成功");
            } else {
                return Result.error("设备更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除设备
     *
     * @param id 设备ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteDevice(@PathVariable Long id) {
        try {
            boolean success = deviceService.deleteDevice(id);
            if (success) {
                return Result.success("设备删除成功");
            } else {
                return Result.error("设备删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量更新设备状态
     *
     * @param deviceIds 设备ID列表
     * @param status    状态
     * @return 更新结果
     */
    @PutMapping("/batch-status")
    public Result<String> batchUpdateStatus(@RequestParam List<Long> deviceIds,
                                            @RequestParam Integer status) {
        try {
            boolean success = deviceService.batchUpdateStatus(deviceIds, status);
            if (success) {
                return Result.success("批量更新成功");
            } else {
                return Result.error("批量更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新设备在线状态
     *
     * @param id           设备ID
     * @param onlineStatus 在线状态
     * @return 更新结果
     */
    @PutMapping("/{id}/online-status")
    public Result<String> updateOnlineStatus(@PathVariable Long id,
                                             @RequestParam Integer onlineStatus) {
        try {
            boolean success = deviceService.updateOnlineStatus(id, onlineStatus, LocalDateTime.now());
            if (success) {
                return Result.success("在线状态更新成功");
            } else {
                return Result.error("在线状态更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查离线设备
     *
     * @param offlineThresholdMinutes 离线阈值(分钟)
     * @return 离线设备列表
     */
    @GetMapping("/check-offline")
    public Result<List<Device>> checkOfflineDevices(@RequestParam(defaultValue = "30") Integer offlineThresholdMinutes) {
        List<Device> offlineDevices = deviceService.checkOfflineDevices(offlineThresholdMinutes);
        return Result.success(offlineDevices);
    }

    /**
     * 验证设备编码是否唯一
     *
     * @param deviceCode 设备编码
     * @param excludeId  排除的ID
     * @return 验证结果
     */
    @GetMapping("/check-code")
    public Result<Boolean> checkDeviceCodeUnique(@RequestParam String deviceCode,
                                                 @RequestParam(required = false) Long excludeId) {
        boolean isUnique = deviceService.isDeviceCodeUnique(deviceCode, excludeId);
        return Result.success(isUnique);
    }
}