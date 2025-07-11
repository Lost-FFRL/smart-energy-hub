package com.kfblue.seh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.DeviceReading;
import com.kfblue.seh.mapper.DeviceReadingMapper;
import com.kfblue.seh.vo.DayValueVO;
import com.kfblue.seh.vo.HourValueVO;
import com.kfblue.seh.vo.RankVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceReadingService extends ServiceImpl<DeviceReadingMapper, DeviceReading> {
    private final DeviceReadingMapper deviceReadingMapper;

    public List<HourValueVO> selectHourValues(LocalDate date, String deviceType, Set<Long> recordIds) {
        // 1. 从数据库获取有数据的小时
        List<HourValueVO> dbResults = deviceReadingMapper.selectHourValues(deviceType, date, recordIds);

        // 2. 转换为Map，以小时为key，方便查找
        Map<Integer, Double> hourDataMap = dbResults.stream().collect(Collectors.toMap(HourValueVO::getHour, HourValueVO::getValue));

        // 3. 构建完整的0-23小时数据
        List<HourValueVO> completeResults = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            HourValueVO hourValue = new HourValueVO();
            hourValue.setHour(hour);
            // 如果该小时有数据就用数据库的值，否则用0
            hourValue.setValue(hourDataMap.getOrDefault(hour, 0.0));
            completeResults.add(hourValue);
        }
        return completeResults;
    }

    public BigDecimal getHourValue(LocalDateTime date, String deviceType) {
        return deviceReadingMapper.getHourValue(deviceType, date);
    }

    public BigDecimal getDayValue(LocalDate date, String deviceType, Set<Long> regionIds) {
        return deviceReadingMapper.getDateValue(deviceType, date, regionIds);
    }

    public List<DayValueVO> dayStats(LocalDate startDate, LocalDate endDate, String deviceType, Set<Long> regionId) {
        return deviceReadingMapper.dayStats(startDate, endDate, deviceType, regionId);
    }

    public List<RankVO> selectDayRank(String deviceType, LocalDate date, Integer limit) {
        return deviceReadingMapper.selectDayRank(deviceType, date, limit);
    }

    public List<RankVO> selectHourRank(String deviceType, LocalDateTime time, Integer limit) {
        return deviceReadingMapper.selectHourRank(deviceType, time, limit);
    }

    /**
     * 获取设备最后一次读数值
     */
    public BigDecimal getLastReadingValue(Long deviceId) {
        return deviceReadingMapper.getLastReadingValue(deviceId);
    }
    
    /**
     * 保存设备读数
     */
    public boolean saveDeviceReading(DeviceReading reading) {
        return this.save(reading);
    }
    
    /**
     * 保存单个设备读数
     */
    public boolean saveReading(DeviceReading reading) {
        return this.save(reading);
    }
    
    /**
     * 批量保存设备读数
     */
    public boolean batchSaveDeviceReadings(List<DeviceReading> readings) {
        return this.saveBatch(readings);
    }
    
    /**
     * 获取指定时间范围内的设备读数
     */
    public List<DeviceReading> getReadingsByTimeRange(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        return deviceReadingMapper.getReadingsByTimeRange(deviceId, startTime, endTime);
    }
    
    /**
     * 统计指定设备的读数数量
     */
    public int countByDeviceId(Long deviceId) {
        return deviceReadingMapper.countByDeviceId(deviceId);
    }
    
    /**
     * 获取设备最新的N条读数记录
     */
    public List<DeviceReading> getLatestReadingsByDeviceId(Long deviceId, int limit) {
        return deviceReadingMapper.getLatestReadingsByDeviceId(deviceId, limit);
    }
}