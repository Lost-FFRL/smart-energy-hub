package com.kfblue.seh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.LevelMonitor;
import com.kfblue.seh.mapper.LevelMonitorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 液位监控服务类
 * 
 * @author system
 * @since 2025-06-24
 */
@Service
@RequiredArgsConstructor
public class LevelMonitorService extends ServiceImpl<LevelMonitorMapper, LevelMonitor> {

    /**
     * 根据设备ID和时间范围查询液位数据
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 液位监控数据列表
     */
    public List<LevelMonitor> getByDeviceIdAndTimeRange(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectByDeviceIdAndTimeRange(deviceId, startTime, endTime);
    }

    /**
     * 获取设备最新液位数据
     * 
     * @param deviceId 设备ID
     * @return 最新液位数据
     */
    public LevelMonitor getLatestByDeviceId(Long deviceId) {
        return baseMapper.selectLatestByDeviceId(deviceId);
    }

    /**
     * 获取所有设备的最新液位数据
     * 
     * @return 最新液位数据列表
     */
    public List<LevelMonitor> getAllLatest() {
        return baseMapper.selectAllLatest();
    }

    /**
     * 获取液位报警数据
     * 
     * @param alarmStatus 报警状态
     * @return 报警数据列表
     */
    public List<LevelMonitor> getByAlarmStatus(Integer alarmStatus) {
        return baseMapper.selectByAlarmStatus(alarmStatus);
    }

    /**
     * 获取液位趋势数据
     * 
     * @param deviceId 设备ID
     * @param hours 小时数
     * @return 液位趋势数据
     */
    public List<LevelMonitor> getTrendData(Long deviceId, Integer hours) {
        return baseMapper.selectTrendData(deviceId, hours);
    }

    /**
     * 保存液位监控数据
     * 
     * @param levelMonitor 液位监控数据
     * @return 是否成功
     */
    public boolean saveLevelData(LevelMonitor levelMonitor) {
        // 设置监测时间为当前时间
        if (levelMonitor.getMonitorTime() == null) {
            levelMonitor.setMonitorTime(LocalDateTime.now());
        }
        
        // 计算液位百分比
        if (levelMonitor.getCurrentLevel() != null && levelMonitor.getTotalHeight() != null 
            && levelMonitor.getTotalHeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = levelMonitor.getCurrentLevel()
                .divide(levelMonitor.getTotalHeight(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            levelMonitor.setLevelPercentage(percentage);
        }
        
        // 计算当前容量
        if (levelMonitor.getLevelPercentage() != null && levelMonitor.getTotalCapacity() != null) {
            BigDecimal currentCapacity = levelMonitor.getTotalCapacity()
                .multiply(levelMonitor.getLevelPercentage())
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            levelMonitor.setCurrentCapacity(currentCapacity);
        }
        
        // 检查报警状态
        checkAlarmStatus(levelMonitor);
        
        // 设置默认数据状态为正常
        if (levelMonitor.getDataStatus() == null) {
            levelMonitor.setDataStatus(1);
        }
        
        return this.save(levelMonitor);
    }

    /**
     * 批量保存液位监控数据
     * 
     * @param levelMonitors 液位监控数据列表
     * @return 是否成功
     */
    public boolean batchSaveLevelData(List<LevelMonitor> levelMonitors) {
        if (levelMonitors == null || levelMonitors.isEmpty()) {
            return false;
        }
        
        // 设置默认值和计算相关字段
        LocalDateTime now = LocalDateTime.now();
        for (LevelMonitor levelMonitor : levelMonitors) {
            if (levelMonitor.getMonitorTime() == null) {
                levelMonitor.setMonitorTime(now);
            }
            
            // 计算液位百分比
            if (levelMonitor.getCurrentLevel() != null && levelMonitor.getTotalHeight() != null 
                && levelMonitor.getTotalHeight().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = levelMonitor.getCurrentLevel()
                    .divide(levelMonitor.getTotalHeight(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
                levelMonitor.setLevelPercentage(percentage);
            }
            
            // 计算当前容量
            if (levelMonitor.getLevelPercentage() != null && levelMonitor.getTotalCapacity() != null) {
                BigDecimal currentCapacity = levelMonitor.getTotalCapacity()
                    .multiply(levelMonitor.getLevelPercentage())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                levelMonitor.setCurrentCapacity(currentCapacity);
            }
            
            // 检查报警状态
            checkAlarmStatus(levelMonitor);
            
            if (levelMonitor.getDataStatus() == null) {
                levelMonitor.setDataStatus(1);
            }
        }
        
        return this.saveBatch(levelMonitors);
    }

    /**
     * 检查报警状态
     * 
     * @param levelMonitor 液位监控数据
     */
    private void checkAlarmStatus(LevelMonitor levelMonitor) {
        if (levelMonitor.getCurrentLevel() == null) {
            levelMonitor.setAlarmStatus(0);
            return;
        }
        
        BigDecimal currentLevel = levelMonitor.getCurrentLevel();
        BigDecimal highThreshold = levelMonitor.getHighAlarmThreshold();
        BigDecimal lowThreshold = levelMonitor.getLowAlarmThreshold();
        
        // 超高液位报警
        if (highThreshold != null && currentLevel.compareTo(highThreshold.multiply(BigDecimal.valueOf(1.1))) > 0) {
            levelMonitor.setAlarmStatus(3);
            levelMonitor.setAlarmMessage("超高液位报警");
        }
        // 高液位报警
        else if (highThreshold != null && currentLevel.compareTo(highThreshold) > 0) {
            levelMonitor.setAlarmStatus(1);
            levelMonitor.setAlarmMessage("高液位报警");
        }
        // 超低液位报警
        else if (lowThreshold != null && currentLevel.compareTo(lowThreshold.multiply(BigDecimal.valueOf(0.9))) < 0) {
            levelMonitor.setAlarmStatus(4);
            levelMonitor.setAlarmMessage("超低液位报警");
        }
        // 低液位报警
        else if (lowThreshold != null && currentLevel.compareTo(lowThreshold) < 0) {
            levelMonitor.setAlarmStatus(2);
            levelMonitor.setAlarmMessage("低液位报警");
        }
        // 正常
        else {
            levelMonitor.setAlarmStatus(0);
            levelMonitor.setAlarmMessage(null);
        }
    }

    /**
     * 获取24小时液位趋势
     * 
     * @param deviceId 设备ID
     * @return 24小时液位趋势
     */
    public List<LevelMonitor> get24HoursTrend(Long deviceId) {
        return getTrendData(deviceId, 24);
    }

    /**
     * 获取7天液位趋势
     * 
     * @param deviceId 设备ID
     * @return 7天液位趋势
     */
    public List<LevelMonitor> get7DaysTrend(Long deviceId) {
        return getTrendData(deviceId, 24 * 7);
    }
}