package com.kfblue.seh.service;

import com.kfblue.seh.constants.DeviceTypeConstants;
import com.kfblue.seh.entity.Device;
import com.kfblue.seh.entity.DeviceReading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * 设备数据生成服务
 * 用于模拟不同类型设备的数据生成
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceDataGeneratorService {
    
    private final DeviceReadingService deviceReadingService;
    private final Random random = new Random();
    
    /**
     * 生成设备读数
     * @param device 设备信息
     * @param readingTime 读数时间
     * @return 生成的设备读数
     */
    public DeviceReading generateDeviceReading(Device device, LocalDateTime readingTime) {
        DeviceReading reading = new DeviceReading();
        reading.setDeviceId(device.getId());
        reading.setReadingTime(readingTime);
        
        // 获取上次读数值，如果没有历史数据则使用设备初始值
        BigDecimal lastValue = null;
        try {
            lastValue = getLastReadingValue(device.getId());
        } catch (Exception e) {
            log.warn("获取设备{}最后读数值失败: {}", device.getId(), e.getMessage());
        }
        
        if (lastValue == null) {
            // 如果没有历史数据，使用设备初始值，如果初始值也为空则使用默认值
            lastValue = device.getInitialValue() != null ? device.getInitialValue() : getDefaultInitialValue(device.getDeviceType());
            log.info("设备{}没有历史数据，使用初始值: {}", device.getId(), lastValue);
        }
        
        // 根据设备类型生成不同的数据
        switch (device.getDeviceType()) {
            case DeviceTypeConstants.WATER:
                generateWaterMeterData(reading, lastValue);
                break;
            case DeviceTypeConstants.ELECTRIC:
                generateElectricMeterData(reading, lastValue);
                break;
            case DeviceTypeConstants.GAS:
                generateGasMeterData(reading, lastValue);
                break;
            case DeviceTypeConstants.HEAT:
                generateHeatMeterData(reading, lastValue);
                break;
            default:
                log.warn("未知设备类型: {}", device.getDeviceType());
                return null;
        }
        
        // 设置通用字段
        reading.setSignalStrength(80 + random.nextInt(21)); // 80-100
        reading.setBatteryLevel(70 + random.nextInt(31)); // 70-100
        reading.setDataQuality(1); // 正常数据
        reading.setCollectMethod("auto");
        
        return reading;
    }
    
    /**
     * 生成水表数据
     */
    private void generateWaterMeterData(DeviceReading reading, BigDecimal lastValue) {
        // 水表每小时用水量：0.1-2.0 m³，小范围波动
        double hourlyConsumption = 0.1 + random.nextDouble() * 1.9;
        // 添加±10%的随机波动
        hourlyConsumption *= (0.9 + random.nextDouble() * 0.2);
        
        BigDecimal currentValue = lastValue.add(BigDecimal.valueOf(hourlyConsumption)
            .setScale(4, RoundingMode.HALF_UP));
        
        reading.setCurrentValue(currentValue);
        reading.setIncrementValue(BigDecimal.valueOf(hourlyConsumption)
            .setScale(4, RoundingMode.HALF_UP));
    }
    
    /**
     * 生成电表数据
     */
    private void generateElectricMeterData(DeviceReading reading, BigDecimal lastValue) {
        // 电表每小时用电量：1-10 kWh，根据时段调整
        int hour = reading.getReadingTime().getHour();
        double baseConsumption;
        
        // 根据时段设置基础用电量
        if (hour >= 8 && hour <= 18) {
            baseConsumption = 3 + random.nextDouble() * 7; // 白天用电多
            reading.setRateType("peak"); // 峰时
        } else if (hour >= 19 && hour <= 22) {
            baseConsumption = 2 + random.nextDouble() * 5; // 晚上用电中等
            reading.setRateType("flat"); // 平时
        } else {
            baseConsumption = 0.5 + random.nextDouble() * 2; // 夜间用电少
            reading.setRateType("valley"); // 谷时
        }
        
        // 添加±15%的随机波动
        baseConsumption *= (0.85 + random.nextDouble() * 0.3);
        
        BigDecimal currentValue = lastValue.add(BigDecimal.valueOf(baseConsumption)
            .setScale(4, RoundingMode.HALF_UP));
        
        reading.setCurrentValue(currentValue);
        reading.setIncrementValue(BigDecimal.valueOf(baseConsumption)
            .setScale(4, RoundingMode.HALF_UP));
        reading.setPowerFactor(BigDecimal.valueOf(0.85 + random.nextDouble() * 0.1)
            .setScale(3, RoundingMode.HALF_UP)); // 功率因数0.85-0.95
    }
    
    /**
     * 生成气表数据
     */
    private void generateGasMeterData(DeviceReading reading, BigDecimal lastValue) {
        // 气表每小时用气量：0.5-3.0 m³
        double hourlyConsumption = 0.5 + random.nextDouble() * 2.5;
        // 添加±12%的随机波动
        hourlyConsumption *= (0.88 + random.nextDouble() * 0.24);
        
        BigDecimal currentValue = lastValue.add(BigDecimal.valueOf(hourlyConsumption)
            .setScale(4, RoundingMode.HALF_UP));
        
        reading.setCurrentValue(currentValue);
        reading.setIncrementValue(BigDecimal.valueOf(hourlyConsumption)
            .setScale(4, RoundingMode.HALF_UP));
        
        // 气表特有字段
        reading.setPressure(BigDecimal.valueOf(0.15 + random.nextDouble() * 0.1)
            .setScale(4, RoundingMode.HALF_UP)); // 压力0.15-0.25 MPa
        reading.setTemperature(BigDecimal.valueOf(15 + random.nextDouble() * 10)
            .setScale(2, RoundingMode.HALF_UP)); // 温度15-25℃
    }
    
    /**
     * 生成热表数据
     */
    private void generateHeatMeterData(DeviceReading reading, BigDecimal lastValue) {
        // 热表每小时热量：2-15 GJ
        double hourlyConsumption = 2 + random.nextDouble() * 13;
        // 添加±20%的随机波动（热表波动较大）
        hourlyConsumption *= (0.8 + random.nextDouble() * 0.4);
        
        BigDecimal currentValue = lastValue.add(BigDecimal.valueOf(hourlyConsumption)
            .setScale(4, RoundingMode.HALF_UP));
        
        reading.setCurrentValue(currentValue);
        reading.setIncrementValue(BigDecimal.valueOf(hourlyConsumption)
            .setScale(4, RoundingMode.HALF_UP));
        
        // 热表特有字段
        reading.setSupplyTemp(BigDecimal.valueOf(60 + random.nextDouble() * 20)
            .setScale(2, RoundingMode.HALF_UP)); // 供水温度60-80℃
        reading.setReturnTemp(BigDecimal.valueOf(40 + random.nextDouble() * 15)
            .setScale(2, RoundingMode.HALF_UP)); // 回水温度40-55℃
        reading.setFlowRate(BigDecimal.valueOf(1 + random.nextDouble() * 4)
            .setScale(4, RoundingMode.HALF_UP)); // 瞬时流量1-5 m³/h
    }
    
    /**
     * 获取设备最后一次读数值
     */
    private BigDecimal getLastReadingValue(Long deviceId) {
        return deviceReadingService.getLastReadingValue(deviceId);
    }
    
    /**
     * 根据设备类型获取默认初始值
     */
    private BigDecimal getDefaultInitialValue(String deviceType) {
        switch (deviceType) {
            case DeviceTypeConstants.WATER:
                return new BigDecimal("1000.0"); // 水表默认1000立方米
            case DeviceTypeConstants.ELECTRIC:
                return new BigDecimal("5000.0"); // 电表默认5000度
            case DeviceTypeConstants.GAS:
                return new BigDecimal("800.0"); // 燃气表默认800立方米
            case DeviceTypeConstants.HEAT:
                return new BigDecimal("2000.0"); // 热量表默认2000千瓦时
            default:
                return BigDecimal.ZERO;
        }
    }
}