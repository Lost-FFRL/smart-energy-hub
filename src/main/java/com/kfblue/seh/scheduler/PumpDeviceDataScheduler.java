package com.kfblue.seh.scheduler;

import com.kfblue.seh.entity.FlowMonitor;
import com.kfblue.seh.entity.LevelMonitor;
import com.kfblue.seh.entity.PumpDevice;
import com.kfblue.seh.service.FlowMonitorService;
import com.kfblue.seh.service.LevelMonitorService;
import com.kfblue.seh.service.PumpDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 水泵设备数据定时任务调度器
 * 每小时生成水泵设备数据和流水表数据
 * 
 * @author system
 * @since 2025-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PumpDeviceDataScheduler {

    private final PumpDeviceService pumpDeviceService;
    private final FlowMonitorService flowMonitorService;
    private final LevelMonitorService levelMonitorService;
    private final Random random = new Random();

    /**
     * 每小时生成水泵设备数据和流水表数据
     * 每小时的第0分钟执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void generatePumpDeviceData() {
        log.info("开始生成水泵设备数据和流水表数据...");
        
        try {
            // 获取所有水泵设备
            List<PumpDevice> pumpDevices = pumpDeviceService.list();
            
            if (pumpDevices.isEmpty()) {
                log.warn("没有找到水泵设备，跳过数据生成");
                return;
            }
            
            List<FlowMonitor> flowMonitors = new ArrayList<>();
            List<LevelMonitor> levelMonitors = new ArrayList<>();
            
            LocalDateTime now = LocalDateTime.now();
            
            for (PumpDevice device : pumpDevices) {
                // 更新设备状态
                updateDeviceStatus(device);
                
                // 生成流量监控数据
                FlowMonitor flowMonitor = generateFlowMonitorData(device, now);
                flowMonitors.add(flowMonitor);
                
                // 生成液位监控数据
                LevelMonitor levelMonitor = generateLevelMonitorData(device, now);
                levelMonitors.add(levelMonitor);
            }
            
            // 批量保存设备状态更新
            pumpDeviceService.updateBatchById(pumpDevices);
            
            // 批量保存流量监控数据
            boolean flowSaved = flowMonitorService.batchSaveFlowData(flowMonitors);
            
            // 批量保存液位监控数据
            boolean levelSaved = levelMonitorService.batchSaveLevelData(levelMonitors);
            
            log.info("水泵设备数据生成完成 - 设备数量: {}, 流量数据: {}, 液位数据: {}", 
                    pumpDevices.size(), 
                    flowSaved ? "成功" : "失败", 
                    levelSaved ? "成功" : "失败");
                    
        } catch (Exception e) {
            log.error("生成水泵设备数据时发生错误", e);
        }
    }
    
    /**
     * 手动触发数据生成（用于测试）
     */
    public void manualGenerateData() {
        log.info("手动触发水泵设备数据生成...");
        generatePumpDeviceData();
    }
    
    /**
     * 更新设备状态
     */
    private void updateDeviceStatus(PumpDevice device) {
        // 模拟在线状态（95%在线率）
        device.setOnlineStatus(random.nextDouble() < 0.95 ? 1 : 0);
        
        // 模拟工作状态（在线设备80%工作）
        if (device.getOnlineStatus() == 1) {
            device.setWorkStatus(random.nextDouble() < 0.8 ? 1 : 0);
        } else {
            device.setWorkStatus(0);
        }
        
        // 模拟报警状态（5%报警率）
        device.setAlarmStatus(random.nextDouble() < 0.05 ? 1 : 0);
        
        // 更新最后在线时间
        if (device.getOnlineStatus() == 1) {
            device.setLastOnlineTime(LocalDateTime.now());
        }
        
        // 模拟运行参数（仅在工作状态下更新）
        if (device.getWorkStatus() == 1) {
            // 频率：45-55Hz
            device.setCurrentFrequency(BigDecimal.valueOf(45 + random.nextDouble() * 10)
                    .setScale(2, RoundingMode.HALF_UP));
            
            // 三相电流：根据额定功率模拟（A相）
            BigDecimal ratedPower = device.getRatedPower() != null ? device.getRatedPower() : BigDecimal.valueOf(15);
            BigDecimal baseCurrent = ratedPower.divide(BigDecimal.valueOf(3.8), 2, RoundingMode.HALF_UP);
            device.setCurrentA(baseCurrent.multiply(BigDecimal.valueOf(0.9 + random.nextDouble() * 0.2))
                    .setScale(2, RoundingMode.HALF_UP));
            device.setCurrentB(baseCurrent.multiply(BigDecimal.valueOf(0.9 + random.nextDouble() * 0.2))
                    .setScale(2, RoundingMode.HALF_UP));
            device.setCurrentC(baseCurrent.multiply(BigDecimal.valueOf(0.9 + random.nextDouble() * 0.2))
                    .setScale(2, RoundingMode.HALF_UP));
            
            // 电压：380V±5%
            device.setVoltage(BigDecimal.valueOf(380 * (0.95 + random.nextDouble() * 0.1))
                    .setScale(1, RoundingMode.HALF_UP));
            
            // 压力：正压0.2-0.8MPa，负压-0.1-0.1MPa
            device.setPositivePressure(BigDecimal.valueOf(0.2 + random.nextDouble() * 0.6)
                    .setScale(3, RoundingMode.HALF_UP));
            device.setNegativePressure(BigDecimal.valueOf(-0.1 + random.nextDouble() * 0.2)
                    .setScale(3, RoundingMode.HALF_UP));
            
            // 功率：根据频率和负载计算
            BigDecimal powerFactor = device.getCurrentFrequency().divide(BigDecimal.valueOf(50), 4, RoundingMode.HALF_UP);
            device.setPower(ratedPower.multiply(powerFactor).multiply(BigDecimal.valueOf(0.8 + random.nextDouble() * 0.4))
                    .setScale(2, RoundingMode.HALF_UP));
            
            // 累计运行小时数（每小时增加1小时）
            BigDecimal currentHours = device.getRunningHours() != null ? device.getRunningHours() : BigDecimal.ZERO;
            device.setRunningHours(currentHours.add(BigDecimal.ONE));
        }
    }
    
    /**
     * 生成流量监控数据
     */
    private FlowMonitor generateFlowMonitorData(PumpDevice device, LocalDateTime monitorTime) {
        FlowMonitor flowMonitor = new FlowMonitor();
        flowMonitor.setDeviceId(device.getId());
        flowMonitor.setDeviceCode(device.getDeviceCode());
        flowMonitor.setMonitorTime(monitorTime);
        
        if (device.getWorkStatus() == 1) {
            // 设备工作时生成流量数据
            BigDecimal ratedFlow = device.getRatedFlow() != null ? device.getRatedFlow() : BigDecimal.valueOf(100);
            
            // 瞬时流量：额定流量的70%-110%
            BigDecimal instantFlow = ratedFlow.multiply(BigDecimal.valueOf(0.7 + random.nextDouble() * 0.4))
                    .setScale(2, RoundingMode.HALF_UP);
            flowMonitor.setInstantFlow(instantFlow);
            
            // 累计流量：每小时累加瞬时流量
            FlowMonitor lastFlow = flowMonitorService.getLatestByDeviceId(device.getId());
            BigDecimal totalFlow = lastFlow != null && lastFlow.getTotalFlow() != null ? 
                    lastFlow.getTotalFlow() : BigDecimal.ZERO;
            flowMonitor.setTotalFlow(totalFlow.add(instantFlow));
            
            // 流速：根据管径计算（假设管径200mm）
            BigDecimal pipeArea = BigDecimal.valueOf(Math.PI * 0.1 * 0.1); // 管道截面积
            BigDecimal velocity = instantFlow.divide(pipeArea.multiply(BigDecimal.valueOf(3600)), 4, RoundingMode.HALF_UP);
            flowMonitor.setVelocity(velocity);
            
            // 温度：15-25°C
            flowMonitor.setTemperature(BigDecimal.valueOf(15 + random.nextDouble() * 10)
                    .setScale(1, RoundingMode.HALF_UP));
            
            // 压力：0.2-0.6MPa
            flowMonitor.setPressure(BigDecimal.valueOf(0.2 + random.nextDouble() * 0.4)
                    .setScale(3, RoundingMode.HALF_UP));
        } else {
            // 设备停止时流量为0
            flowMonitor.setInstantFlow(BigDecimal.ZERO);
            FlowMonitor lastFlow = flowMonitorService.getLatestByDeviceId(device.getId());
            BigDecimal totalFlow = lastFlow != null && lastFlow.getTotalFlow() != null ? 
                    lastFlow.getTotalFlow() : BigDecimal.ZERO;
            flowMonitor.setTotalFlow(totalFlow);
            flowMonitor.setVelocity(BigDecimal.ZERO);
            flowMonitor.setTemperature(BigDecimal.valueOf(20)); // 环境温度
            flowMonitor.setPressure(BigDecimal.ZERO);
        }
        
        // 数据状态：95%正常
        flowMonitor.setDataStatus(random.nextDouble() < 0.95 ? 1 : 0);
        
        return flowMonitor;
    }
    
    /**
     * 生成液位监控数据
     */
    private LevelMonitor generateLevelMonitorData(PumpDevice device, LocalDateTime monitorTime) {
        LevelMonitor levelMonitor = new LevelMonitor();
        levelMonitor.setDeviceId(device.getId());
        levelMonitor.setDeviceCode(device.getDeviceCode());
        levelMonitor.setMonitorTime(monitorTime);
        
        // 设置容器参数（模拟值）
        BigDecimal totalHeight = BigDecimal.valueOf(5.0); // 5米高
        BigDecimal totalCapacity = BigDecimal.valueOf(1000.0); // 1000立方米
        levelMonitor.setTotalHeight(totalHeight);
        levelMonitor.setTotalCapacity(totalCapacity);
        
        // 获取上次液位数据
        LevelMonitor lastLevel = levelMonitorService.getLatestByDeviceId(device.getId());
        BigDecimal lastCurrentLevel = lastLevel != null && lastLevel.getCurrentLevel() != null ? 
                lastLevel.getCurrentLevel() : BigDecimal.valueOf(2.5); // 默认50%液位
        
        // 根据设备工作状态模拟液位变化
        BigDecimal currentLevel;
        if (device.getWorkStatus() == 1) {
            // 设备工作时液位下降（抽水）
            BigDecimal decrease = BigDecimal.valueOf(0.1 + random.nextDouble() * 0.2); // 下降0.1-0.3米
            currentLevel = lastCurrentLevel.subtract(decrease);
        } else {
            // 设备停止时液位可能上升（进水）或保持不变
            BigDecimal change = BigDecimal.valueOf(-0.05 + random.nextDouble() * 0.15); // -0.05到+0.1米
            currentLevel = lastCurrentLevel.add(change);
        }
        
        // 限制液位范围
        if (currentLevel.compareTo(BigDecimal.ZERO) < 0) {
            currentLevel = BigDecimal.ZERO;
        }
        if (currentLevel.compareTo(totalHeight) > 0) {
            currentLevel = totalHeight;
        }
        
        levelMonitor.setCurrentLevel(currentLevel.setScale(3, RoundingMode.HALF_UP));
        
        // 设置报警阈值
        levelMonitor.setHighAlarmThreshold(totalHeight.multiply(BigDecimal.valueOf(0.9))); // 90%高位报警
        levelMonitor.setLowAlarmThreshold(totalHeight.multiply(BigDecimal.valueOf(0.1)));  // 10%低位报警
        
        // 温度：10-30°C
        levelMonitor.setTemperature(BigDecimal.valueOf(10 + random.nextDouble() * 20)
                .setScale(1, RoundingMode.HALF_UP));
        
        // 密度：0.98-1.02 g/cm³
        levelMonitor.setDensity(BigDecimal.valueOf(0.98 + random.nextDouble() * 0.04)
                .setScale(4, RoundingMode.HALF_UP));
        
        // 数据状态：95%正常
        levelMonitor.setDataStatus(random.nextDouble() < 0.95 ? 1 : 0);
        
        return levelMonitor;
    }
}