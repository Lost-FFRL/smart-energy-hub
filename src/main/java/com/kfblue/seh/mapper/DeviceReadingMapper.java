package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.DeviceReading;
import com.kfblue.seh.vo.DayValueVO;
import com.kfblue.seh.vo.HourValueVO;
import com.kfblue.seh.vo.RankVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper
public interface DeviceReadingMapper extends BaseMapper<DeviceReading> {

    List<HourValueVO> selectHourValues(@Param("deviceType") String deviceType,
                                       @Param("statsDate") LocalDate statsDate,
                                       @Param("regionIds") Set<Long> regionIds);

    BigDecimal getHourValue(@Param("deviceType") String deviceType, @Param("time") LocalDateTime time);

    BigDecimal getDateValue(@Param("deviceType") String deviceType, @Param("statsDate") LocalDate statsDate, @Param("regionIds") Set<Long> regionIds);

    List<DayValueVO> dayStats(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("deviceType") String deviceType,
                              @Param("regionIds") Set<Long> regionIds);

    /**
     * 获取设备日排行榜
     */
    List<RankVO> selectDayRank(@Param("deviceType") String deviceType,
                               @Param("date") LocalDate date,
                               @Param("limit") Integer limit);

    /**
     * 获取区域日排行榜
     */
    List<RankVO> selectHourRank(@Param("deviceType") String deviceType,
                                @Param("time") LocalDateTime time,
                                @Param("limit") Integer limit);
    
    /**
     * 获取设备最后一次读数值
     */
    BigDecimal getLastReadingValue(@Param("deviceId") Long deviceId);
    
    /**
     * 获取指定时间范围内的设备读数
     */
    List<DeviceReading> getReadingsByTimeRange(
        @Param("deviceId") Long deviceId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 统计指定设备的读数数量
     */
    int countByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 获取设备最新的N条读数记录
     */
    List<DeviceReading> getLatestReadingsByDeviceId(
        @Param("deviceId") Long deviceId,
        @Param("limit") int limit
    );
}