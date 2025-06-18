package com.kfblue.seh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.DeviceDailyStatistics;
import com.kfblue.seh.mapper.DeviceDailyStatisticMapper;
import com.kfblue.seh.vo.DayValueVO;
import com.kfblue.seh.vo.DistributionVO;
import com.kfblue.seh.vo.MonthValueVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 设备日统计服务实现类
 */
@Service
public class DeviceDailyStatisticService extends ServiceImpl<DeviceDailyStatisticMapper, DeviceDailyStatistics> {

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

}