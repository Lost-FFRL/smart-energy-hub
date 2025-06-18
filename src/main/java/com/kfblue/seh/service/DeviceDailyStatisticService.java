package com.kfblue.seh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.DeviceDailyStatistics;
import com.kfblue.seh.mapper.DeviceDailyStatisticMapper;
import com.kfblue.seh.vo.DistributionVO;
import com.kfblue.seh.vo.MonthValueVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 设备日统计服务实现类
 */
@Service
public class DeviceDailyStatisticService extends ServiceImpl<DeviceDailyStatisticMapper, DeviceDailyStatistics> {

    public BigDecimal summary(String deviceType, LocalDate startDate, LocalDate endDate) {
        return baseMapper.summary(deviceType, startDate, endDate);
    }

    public List<DistributionVO> distribution(String deviceType, LocalDate startDate, LocalDate endDate, Integer top) {
        return baseMapper.distribution(deviceType, startDate, endDate, top);
    }

    public List<MonthValueVO> monthTrend(String deviceType, LocalDate startDate, LocalDate endDate) {
        return baseMapper.monthTrend(deviceType, startDate, endDate);
    }

}