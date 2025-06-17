package com.kfblue.seh.service;

import com.kfblue.seh.mapper.DeviceReadingMapper;
import com.kfblue.seh.vo.DayValueVO;
import com.kfblue.seh.vo.HourValueVO;
import com.kfblue.seh.vo.RankVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceReadingService {
    private final DeviceReadingMapper deviceReadingMapper;

    public List<HourValueVO> selectHourValues(LocalDate date, String deviceType) {
        // 1. 从数据库获取有数据的小时
        List<HourValueVO> dbResults = deviceReadingMapper.selectHourValues(deviceType, date);

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

    public Double getHourValue(LocalDateTime date, String deviceType) {
        return deviceReadingMapper.getHourValue(deviceType, date);
    }

    public Double getDayValue(LocalDate date, String deviceType, Long regionId) {
        return deviceReadingMapper.getDateValue(deviceType, date, regionId);
    }

    public List<DayValueVO> dayStats(LocalDate startDate, LocalDate endDate, String deviceType, Long regionId) {
        return deviceReadingMapper.dayStats(startDate, endDate, deviceType, regionId);
    }

    public List<RankVO> selectDayRank(String deviceType, LocalDate date, Integer limit) {
        return deviceReadingMapper.selectDayRank(deviceType, date, limit);
    }

    public List<RankVO> selectHourRank(String deviceType, LocalDateTime time, Integer limit) {
        return deviceReadingMapper.selectHourRank(deviceType, time, limit);
    }

}