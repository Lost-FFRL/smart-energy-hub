package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备日统计实体类
 * 
 * @author system
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_daily_statistics")
public class DeviceDailyStatistic extends BaseEntity {

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 日开始读数
     */
    private BigDecimal startValue;

    /**
     * 日结束读数
     */
    private BigDecimal endValue;

    /**
     * 日消耗量
     */
    private BigDecimal dailyConsumption;

    /**
     * 最大瞬时值
     */
    private BigDecimal maxInstantValue;

    /**
     * 最小瞬时值
     */
    private BigDecimal minInstantValue;

    /**
     * 平均瞬时值
     */
    private BigDecimal avgInstantValue;

    /**
     * 数据点数量
     */
    private Integer dataPointCount;

    /**
     * 异常数据点数量
     */
    private Integer abnormalDataCount;

    /**
     * 数据完整率(%)
     */
    private BigDecimal dataIntegrityRate;

    /**
     * 在线时长(分钟)
     */
    private Integer onlineDuration;

    /**
     * 离线时长(分钟)
     */
    private Integer offlineDuration;

    /**
     * 在线率(%)
     */
    private BigDecimal onlineRate;

    /**
     * 统计状态(0:统计中,1:已完成)
     */
    private Integer statStatus;

    /**
     * 统计时间
     */
    private LocalDateTime statTime;

    /**
     * 逻辑删除标记(0:正常,1:删除)
     */
    @TableLogic
    private Integer deleted;
}