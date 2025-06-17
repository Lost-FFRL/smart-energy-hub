package com.kfblue.seh.mapper;

import com.kfblue.seh.vo.DayValueVO;
import com.kfblue.seh.vo.HourValueVO;
import com.kfblue.seh.vo.RankVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DeviceReadingMapper {

    List<HourValueVO> selectHourValues(@Param("deviceType") String deviceType, @Param("statsDate") LocalDate statsDate);

    Double getHourValue(@Param("deviceType") String deviceType, @Param("time") LocalDateTime time);

    Double getDateValue(@Param("deviceType") String deviceType, @Param("statsDate") LocalDate statsDate
            , @Param("regionId") Long regionId);

    List<DayValueVO> dayStats(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("deviceType") String deviceType,
                              @Param("regionId") Long regionId);

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
}