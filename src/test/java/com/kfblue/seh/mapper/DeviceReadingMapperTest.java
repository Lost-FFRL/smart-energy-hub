package com.kfblue.seh.mapper;

import com.kfblue.seh.SmartEnergyHubApplicationTests;
import com.kfblue.seh.dto.ElectricityComparisonDTO;
import com.kfblue.seh.dto.WaterUsageTrendDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeviceReadingMapperTest extends SmartEnergyHubApplicationTests {

    @Autowired
    private DeviceReadingMapper deviceReadingMapper;

    @Test
    void getWaterUsageTrend() {
        Long deviceId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();

        WaterUsageTrendDTO result = deviceReadingMapper.getWaterUsageTrend(deviceId, startTime, endTime);
        assertNotNull(result);
        assertNotNull(result.getDeviceId());
        assertNotNull(result.getPoints());
    }

    @Test
    void getAlertThreshold() {
        Long deviceId = 1L;
        Double result = deviceReadingMapper.getAlertThreshold(deviceId);
        assertNotNull(result);
    }

    @Test
    void getElectricityComparison() {
        Long deviceId = 1L;
        ElectricityComparisonDTO result = deviceReadingMapper.getElectricityComparison(deviceId);
        assertNotNull(result);
        assertNotNull(result.getDeviceId());
        assertNotNull(result.getCurrentHourUsage());
    }
}