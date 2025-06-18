package com.kfblue.seh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.DeviceDailyStatistics;
import com.kfblue.seh.entity.DeviceReading;
import com.kfblue.seh.mapper.DeviceDailyStatisticMapper;
import com.kfblue.seh.vo.DayValueVO;
import com.kfblue.seh.vo.DistributionVO;
import com.kfblue.seh.vo.MonthValueVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * 设备日统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceDailyStatisticService extends ServiceImpl<DeviceDailyStatisticMapper, DeviceDailyStatistics> {
    
    private final DeviceReadingService deviceReadingService;

    public BigDecimal summary(String deviceType, LocalDate startDate, LocalDate endDate, Set<Long> regionIds) {
        return baseMapper.summary(deviceType, startDate, endDate, regionIds);
    }

    public List<DistributionVO> distribution(String deviceType, LocalDate startDate, LocalDate endDate, Integer top, Set<Long> regionIds) {
        return baseMapper.distribution(deviceType, startDate, endDate, top, regionIds);
    }

    public List<MonthValueVO> monthTrend(String deviceType, LocalDate startDate, LocalDate endDate) {
        return baseMapper.monthTrend(deviceType, startDate, endDate);
    }

    public List<DayValueVO> dayTrend(String deviceType, LocalDate startDate, LocalDate endDate, Set<Long> regionIds) {
        return baseMapper.dayTrend(deviceType, startDate, endDate, regionIds);
    }
    
    /**
     * 计算并保存设备日统计数据
     */
    @Transactional
    public void calculateAndSaveDailyStatistics(Long deviceId, LocalDate date) {
        log.info("开始计算设备 {} 在 {} 的日统计数据", deviceId, date);
        
        // 获取指定日期的设备读数数据
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);
        
        List<DeviceReading> readings = deviceReadingService.getReadingsByTimeRange(
            deviceId, startTime, endTime);
        
        if (readings.isEmpty()) {
            log.warn("设备 {} 在 {} 没有读数数据，跳过统计计算", deviceId, date);
            return;
        }
        
        // 计算基础统计数据
        DeviceDailyStatistics statistics = calculateBasicStatistics(deviceId, date, readings);
        
        // 检查是否已存在该日期的统计记录
        DeviceDailyStatistics existing = this.lambdaQuery()
            .eq(DeviceDailyStatistics::getDeviceId, deviceId)
            .eq(DeviceDailyStatistics::getStatDate, date)
            .one();
        
        if (existing != null) {
            // 更新现有记录
            statistics.setId(existing.getId());
            this.updateById(statistics);
            log.info("更新设备 {} 在 {} 的日统计数据", deviceId, date);
        } else {
            // 创建新记录
            this.save(statistics);
            log.info("创建设备 {} 在 {} 的日统计数据", deviceId, date);
        }
    }
    
    /**
     * 计算基础统计数据
     */
    private DeviceDailyStatistics calculateBasicStatistics(Long deviceId, LocalDate date, List<DeviceReading> readings) {
        DeviceDailyStatistics statistics = new DeviceDailyStatistics();
        statistics.setDeviceId(deviceId);
        statistics.setStatDate(date);
        
        // 按时间排序
        readings.sort((r1, r2) -> r1.getReadingTime().compareTo(r2.getReadingTime()));
        
        DeviceReading firstReading = readings.get(0);
        DeviceReading lastReading = readings.get(readings.size() - 1);
        
        // 设置开始和结束读数
        statistics.setStartValue(firstReading.getCurrentValue());
        statistics.setEndValue(lastReading.getCurrentValue());
        
        // 计算日消耗量
        BigDecimal dailyConsumption = lastReading.getCurrentValue().subtract(firstReading.getCurrentValue());
        statistics.setDailyConsumption(dailyConsumption);
        
        // 计算瞬时值统计
        BigDecimal maxInstantValue = readings.stream()
            .map(DeviceReading::getIncrementValue)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
        
        BigDecimal minInstantValue = readings.stream()
            .map(DeviceReading::getIncrementValue)
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
        
        BigDecimal avgInstantValue = readings.stream()
            .map(DeviceReading::getIncrementValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(readings.size()), 4, RoundingMode.HALF_UP);
        
        statistics.setMaxInstantValue(maxInstantValue);
        statistics.setMinInstantValue(minInstantValue);
        statistics.setAvgInstantValue(avgInstantValue);
        
        // 设置数据点数量
        statistics.setDataPointCount(readings.size());
        
        // 计算异常数据点数量（这里简单定义为增量值为负数或过大的情况）
        long abnormalCount = readings.stream()
            .mapToLong(reading -> {
                BigDecimal increment = reading.getIncrementValue();
                return (increment.compareTo(BigDecimal.ZERO) < 0 || 
                       increment.compareTo(BigDecimal.valueOf(100)) > 0) ? 1 : 0;
            })
            .sum();
        statistics.setAbnormalDataCount((int) abnormalCount);
        
        // 计算数据完整率（假设每小时应该有一个数据点）
        int expectedDataPoints = 24; // 一天24小时
        BigDecimal integrityRate = BigDecimal.valueOf(readings.size())
            .divide(BigDecimal.valueOf(expectedDataPoints), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
        if (integrityRate.compareTo(BigDecimal.valueOf(100)) > 0) {
            integrityRate = BigDecimal.valueOf(100);
        }
        statistics.setDataIntegrityRate(integrityRate);
        
        // 设置在线时长（分钟）
        statistics.setOnlineDuration(readings.size() * 60); // 假设每个读数代表1小时，转换为分钟
        
        return statistics;
    }

}