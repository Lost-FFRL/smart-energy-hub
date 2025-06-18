package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.DeviceDailyStatistics;
import com.kfblue.seh.vo.DayValueVO;
import com.kfblue.seh.vo.DistributionVO;
import com.kfblue.seh.vo.MonthValueVO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 设备日统计Mapper接口
 *
 * @author system
 * @since 2025-06-17
 */
@Mapper
public interface DeviceDailyStatisticMapper extends BaseMapper<DeviceDailyStatistics> {

    BigDecimal summary(String deviceType, LocalDate startDate, LocalDate endDate, Set<Long> regionIds);

    List<DistributionVO> distribution(String deviceType, LocalDate startDate, LocalDate endDate, Integer top,Set<Long> regionIds);

    List<MonthValueVO> monthTrend(String deviceType, LocalDate startDate, LocalDate endDate);

    List<DayValueVO> dayTrend(String deviceType, LocalDate startDate, LocalDate endDate, Set<Long> regionIds);
}