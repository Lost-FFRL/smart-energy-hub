package com.kfblue.seh.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfblue.seh.common.Result;
import com.kfblue.seh.dto.DeviceDataDTO;
import com.kfblue.seh.entity.DeviceData;
import com.kfblue.seh.service.DeviceDataService;
import com.kfblue.seh.vo.DeviceDataVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备数据控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/device-data")
@RequiredArgsConstructor
@SaCheckLogin
public class DeviceDataController {
    
    private final DeviceDataService deviceDataService;
    
    /**
     * 分页查询设备数据
     * @param current 当前页
     * @param size 每页大小
     * @param XGateway 网关(可选)
     * @param XTagName 标签名称(可选)
     * @param startTime 开始时间(可选)
     * @param endTime 结束时间(可选)
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<IPage<DeviceDataVO>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String XGateway,
            @RequestParam(required = false) String XTagName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        Page<DeviceData> page = new Page<>(current, size);
        IPage<DeviceDataVO> result = deviceDataService.getDeviceDataPage(page, XGateway, XTagName, startTime, endTime);
        return Result.success(result);
    }
    
    /**
     * 根据ID获取设备数据详情
     * @param id ID
     * @return 设备数据对象
     */
    @GetMapping("/{id}")
    public Result<DeviceData> getById(@PathVariable @NotNull Long id) {
        DeviceData deviceData = deviceDataService.getById(id);
        if (deviceData == null) {
            return Result.error("设备数据不存在");
        }
        return Result.success(deviceData);
    }
    
    /**
     * 获取网关标签的最新数据
     * @param XGateway 网关
     * @param XTagName 标签名称
     * @return 最新设备数据
     */
    @GetMapping("/latest")
    public Result<DeviceDataVO> getLatest(
            @RequestParam @NotBlank String XGateway,
            @RequestParam @NotBlank String XTagName) {
        
        DeviceDataVO deviceData = deviceDataService.getLatestByGatewayAndTag(XGateway, XTagName);
        if (deviceData == null) {
            return Result.error("未找到该网关标签的数据");
        }
        return Result.success(deviceData);
    }
    
    /**
     * 获取网关的所有标签最新数据
     * @param XGateway 网关
     * @return 网关最新数据列表
     */
    @GetMapping("/gateway/{XGateway}/latest")
    public Result<List<DeviceData>> getLatestByGateway(@PathVariable @NotBlank String XGateway) {
        List<DeviceData> dataList = deviceDataService.getLatestDataByGateway(XGateway);
        return Result.success(dataList);
    }
    
    /**
     * 获取标签的所有网关最新数据
     * @param XTagName 标签名称
     * @return 标签最新数据列表
     */
    @GetMapping("/tag/{XTagName}/latest")
    public Result<List<DeviceData>> getLatestByTag(@PathVariable @NotBlank String XTagName) {
        List<DeviceData> dataList = deviceDataService.getLatestDataByTag(XTagName);
        return Result.success(dataList);
    }
    
    /**
     * 保存单条设备数据
     * @param deviceDataDTO 设备数据DTO
     * @return 保存结果
     */
    @PostMapping
    public Result<String> save(@RequestBody @Valid DeviceDataDTO deviceDataDTO) {
        DeviceData deviceData = new DeviceData();
        deviceData.setXGateway(deviceDataDTO.getXGateway());
        deviceData.setXTagName(deviceDataDTO.getXTagName());
        deviceData.setXValue(deviceDataDTO.getXValue());
        deviceData.setXQuality(deviceDataDTO.getXQuality() != null ? deviceDataDTO.getXQuality().toString() : null);
        deviceData.setXTimeStamp(deviceDataDTO.getXTimeStamp() != null ? deviceDataDTO.getXTimeStamp().toString() : LocalDateTime.now().toString());
        
        boolean success = deviceDataService.saveDeviceData(deviceData);
        if (success) {
            return Result.success("保存成功");
        } else {
            return Result.error("保存失败");
        }
    }
    
    /**
     * 批量保存设备数据
     * @param dtoList 设备数据DTO列表
     * @return 保存结果
     */
    @PostMapping("/batch")
    public Result<String> batchSave(@RequestBody @Valid List<DeviceDataDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Result.error("数据列表不能为空");
        }
        
        // 转换DTO为实体对象
        LocalDateTime currentTime = LocalDateTime.now();
        List<DeviceData> dataList = dtoList.stream().map(dto -> {
            DeviceData data = new DeviceData();
            data.setXGateway(dto.getXGateway());
            data.setXTagName(dto.getXTagName());
            data.setXValue(dto.getXValue());
            data.setXQuality(dto.getXQuality() != null ? dto.getXQuality().toString() : null);
            data.setXTimeStamp(dto.getXTimeStamp() != null ? dto.getXTimeStamp().toString() : currentTime.toString());
            return data;
        }).toList();
        
        try {
            int count = deviceDataService.batchSave(dataList);
            return Result.success("批量保存成功，共保存" + count + "条数据");
        } catch (Exception e) {
            log.error("批量保存设备数据失败", e);
            return Result.error("批量保存失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID删除设备数据（逻辑删除）
     * @param id ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable @NotNull Long id) {
        boolean success = deviceDataService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败，数据不存在");
        }
    }
    
    /**
     * 删除历史数据
     * @param beforeTime 删除此时间之前的数据(格式: yyyy-MM-dd HH:mm:ss)
     * @return 删除结果
     */
    @DeleteMapping("/clean")
    public Result<String> deleteHistory(@RequestParam @NotNull String beforeTime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(beforeTime.replace(" ", "T"));
            int count = deviceDataService.deleteByTimeRange(dateTime);
            return Result.success("删除历史数据成功，共删除" + count + "条记录");
        } catch (Exception e) {
            log.error("删除历史数据失败", e);
            return Result.error("删除历史数据失败：" + e.getMessage());
        }
    }
}