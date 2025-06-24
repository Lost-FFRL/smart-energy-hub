package com.kfblue.seh.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfblue.seh.common.Result;
import com.kfblue.seh.dto.DeviceStatisticsDTO;
import com.kfblue.seh.entity.LightingDevice;
import com.kfblue.seh.service.LightingConfigService;
import com.kfblue.seh.service.LightingDeviceService;
import com.kfblue.seh.util.RateUtils;
import com.kfblue.seh.vo.LightDeviceSimpleVO;
import com.kfblue.seh.vo.LightVO;
import com.kfblue.seh.vo.LightingConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "设备监控", description = "设备监控")
@RestController
@RequestMapping("/api/device/monitor")
@RequiredArgsConstructor
public class DeviceMonitorController {

    private final LightingDeviceService lightingDeviceService;
    private final LightingConfigService lightingConfigService;

    @Operation(summary = "智慧照明系统-配电箱", description = "配电箱监控统计")
    @GetMapping("/light/box")
    public Result<LightVO> lightBox() {
        // 获取灯箱设备统计数据
        DeviceStatisticsDTO statistics = lightingDeviceService.getDeviceStatisticsByType("light_box");
        return Result.success(buildLightVO(statistics));
    }

    @Operation(summary = "智慧照明系统-单灯状态", description = "单灯设备监控统计")
    @GetMapping("/light/status")
    public Result<LightVO> lightStatus() {
        // 获取单灯设备统计数据
        DeviceStatisticsDTO statistics = lightingDeviceService.getDeviceStatisticsByType("single_light");
        return Result.success(buildLightVO(statistics));
    }

    @Operation(summary = "智慧照明系统-智能控制", description = "获取智能控制配置列表")
    @GetMapping("/light/config")
    public Result<LightingConfigVO> lightConfig() {
        // 获取启用的智能控制配置
        List<LightingConfigVO> configs = lightingConfigService.getEnabledConfigs();
        return Result.success(configs.stream().filter(config -> "longitude".equals(config.getControlMode())).findFirst().orElse(null));
    }

    @Operation(summary = "智慧照明系统-单灯分页查询", description = "单灯分页查询")
    @GetMapping("/light/page")
    public Result<IPage<LightDeviceSimpleVO>> lightPage(
            @Parameter(description = "当前页", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "设备名称") @RequestParam(required = false) String deviceName,
            @Parameter(description = "区域ID") @RequestParam(required = false) Long regionId,
            @Parameter(description = "在线状态(0:离线,1:在线)") @RequestParam(required = false) Integer onlineStatus) {

        // 调用服务层分页查询方法
        IPage<LightingDevice> page = lightingDeviceService.page(current, size, deviceName, regionId, onlineStatus);

        // 将实体对象转换为VO对象
        IPage<LightDeviceSimpleVO> voPage = page.convert(device -> {
            LightDeviceSimpleVO vo = new LightDeviceSimpleVO();
            vo.setId(device.getId());
            vo.setDeviceCode(device.getDeviceCode());
            vo.setDeviceName(device.getDeviceName());
            vo.setDeviceType(device.getDeviceType());
            vo.setOnlineStatus(device.getOnlineStatus());
            vo.setWorkStatus(device.getWorkStatus());
            vo.setAlarmStatus(device.getAlarmStatus());
            return vo;
        });

        return Result.success(voPage);
    }

    @Operation(summary = "单灯开关控制", description = "控制单灯设备的开关状态")
    @PostMapping("/light/work")
    public Result<String> lightSwitch(@Parameter(description = "设备ID") @RequestParam Long deviceId,
                                      @Parameter(description = "操作类型(0:关闭, 1:开启)") @RequestParam Integer action) {

        // 参数校验
        if (deviceId == null || deviceId <= 0) {
            return Result.error("设备ID不能为空");
        }

        if (action == null || (action != 0 && action != 1)) {
            return Result.error("操作类型只能是0(关闭)或1(开启)");
        }

        // 调用服务层更新设备工作状态
        boolean success = lightingDeviceService.updateWorkStatus(deviceId, action);

        if (success) {
            String actionText = action == 1 ? "开启" : "关闭";
            return Result.success("设备" + actionText + "成功");
        } else {
            return Result.error("操作失败，设备可能不存在或处于离线状态");
        }
    }

    /**
     * 构建 LightVO 对象
     *
     * @param statistics 设备统计数据
     * @return LightVO 对象
     */
    private LightVO buildLightVO(DeviceStatisticsDTO statistics) {
        LightVO lightVO = BeanUtil.copyProperties(statistics, LightVO.class);
        lightVO.setOnlineRate(RateUtils.calculatePercentage(BigDecimal.valueOf(statistics.getOnlineCnt()), BigDecimal.valueOf(statistics.getTotal())));
        lightVO.setWorkRate(RateUtils.calculatePercentage(BigDecimal.valueOf(statistics.getWorkCnt()), BigDecimal.valueOf(statistics.getTotal())));
        lightVO.setAlarmRate(RateUtils.calculatePercentage(BigDecimal.valueOf(statistics.getAlarmCnt()), BigDecimal.valueOf(statistics.getTotal())));
        return lightVO;
    }

}