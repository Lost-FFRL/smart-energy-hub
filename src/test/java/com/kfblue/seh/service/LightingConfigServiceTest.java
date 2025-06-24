package com.kfblue.seh.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfblue.seh.dto.TimePeriodDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * LightingConfigService 测试类
 * 主要测试JSON字符串与集合对象的转换功能
 */
@SpringBootTest
public class LightingConfigServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testTimePeriodsJsonConversion() {
        try {
            // 测试时段配置JSON转换
            String timePeriodsJson = "[{\"startTime\":\"08:00\",\"endTime\":\"18:00\",\"action\":\"on\",\"description\":\"白天工作时段\"},{\"startTime\":\"18:00\",\"endTime\":\"22:00\",\"action\":\"off\",\"description\":\"夜间休息时段\"}]";
            
            // JSON字符串转换为集合对象
            List<TimePeriodDTO> timePeriods = objectMapper.readValue(
                timePeriodsJson, 
                new TypeReference<List<TimePeriodDTO>>() {}
            );
            
            System.out.println("成功解析时段配置JSON: " + timePeriods.size() + " 个时段");
            for (TimePeriodDTO period : timePeriods) {
                System.out.println("时段: " + period.getStartTime() + " - " + period.getEndTime() + ", 动作: " + period.getAction() + ", 描述: " + period.getDescription());
            }
            
            // 集合对象转换为JSON字符串
            String convertedJson = objectMapper.writeValueAsString(timePeriods);
            System.out.println("转换后的JSON: " + convertedJson);
            
        } catch (Exception e) {
            System.err.println("时段配置JSON转换测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testTargetDevicesJsonConversion() {
        try {
            // 测试目标设备JSON转换
            String targetDevicesJson = "[\"DEVICE_001\",\"DEVICE_002\",\"DEVICE_003\"]";
            
            // JSON字符串转换为集合对象
            List<String> targetDevices = objectMapper.readValue(
                targetDevicesJson, 
                new TypeReference<List<String>>() {}
            );
            
            System.out.println("成功解析目标设备JSON: " + targetDevices.size() + " 个设备");
            for (String device : targetDevices) {
                System.out.println("设备: " + device);
            }
            
            // 集合对象转换为JSON字符串
            String convertedJson = objectMapper.writeValueAsString(targetDevices);
            System.out.println("转换后的JSON: " + convertedJson);
            
        } catch (Exception e) {
            System.err.println("目标设备JSON转换测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testEmptyJsonConversion() {
        try {
            // 测试空JSON处理
            String emptyJson = "[]";
            
            List<TimePeriodDTO> timePeriods = objectMapper.readValue(
                emptyJson, 
                new TypeReference<List<TimePeriodDTO>>() {}
            );
            
            List<String> targetDevices = objectMapper.readValue(
                emptyJson, 
                new TypeReference<List<String>>() {}
            );
            
            System.out.println("空JSON处理成功 - 时段数量: " + timePeriods.size() + ", 设备数量: " + targetDevices.size());
            
        } catch (Exception e) {
            System.err.println("空JSON处理测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateJsonFromObjects() {
        try {
            // 创建时段配置对象
            List<TimePeriodDTO> timePeriods = new ArrayList<>();
            TimePeriodDTO period1 = new TimePeriodDTO();
            period1.setStartTime(LocalTime.of(6, 0));
            period1.setEndTime(LocalTime.of(12, 0));
            period1.setAction("on");
            period1.setDescription("上午工作时段");
            timePeriods.add(period1);
            
            TimePeriodDTO period2 = new TimePeriodDTO();
            period2.setStartTime(LocalTime.of(12, 0));
            period2.setEndTime(LocalTime.of(18, 0));
            period2.setAction("off");
            period2.setDescription("下午休息时段");
            timePeriods.add(period2);
            
            // 创建目标设备列表
            List<String> targetDevices = new ArrayList<>();
            targetDevices.add("LED_LIGHT_001");
            targetDevices.add("LED_LIGHT_002");
            targetDevices.add("LED_LIGHT_003");
            
            // 转换为JSON字符串
            String timePeriodsJson = objectMapper.writeValueAsString(timePeriods);
            String targetDevicesJson = objectMapper.writeValueAsString(targetDevices);
            
            System.out.println("时段配置JSON: " + timePeriodsJson);
            System.out.println("目标设备JSON: " + targetDevicesJson);
            
            // 验证可以重新解析
            List<TimePeriodDTO> parsedPeriods = objectMapper.readValue(
                timePeriodsJson, 
                new TypeReference<List<TimePeriodDTO>>() {}
            );
            
            List<String> parsedDevices = objectMapper.readValue(
                targetDevicesJson, 
                new TypeReference<List<String>>() {}
            );
            
            System.out.println("重新解析成功 - 时段数量: " + parsedPeriods.size() + ", 设备数量: " + parsedDevices.size());
            
        } catch (Exception e) {
            System.err.println("对象转JSON测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}