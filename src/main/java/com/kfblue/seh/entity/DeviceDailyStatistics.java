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
public class DeviceDailyStatistics extends BaseEntity {

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
     * 日峰值
     */
    private BigDecimal peakValue;

    /**
     * 峰值时间
     */
    private java.time.LocalTime peakTime;

    /**
     * 日谷值
     */
    private BigDecimal valleyValue;

    /**
     * 谷值时间
     */
    private java.time.LocalTime valleyTime;

    /**
     * 日均值
     */
    private BigDecimal avgValue;

    /**
     * 读数次数
     */
    private Integer readingCount;

    /**
     * 正常读数次数
     */
    private Integer normalCount;

    /**
     * 异常读数次数
     */
    private Integer abnormalCount;

    /**
     * 数据完整性(%)
     */
    private BigDecimal dataIntegrity;

    /**
     * 费用金额(元)
     */
    private BigDecimal costAmount;

    /**
     * 单价(元/单位)
     */
    private BigDecimal unitPrice;

    // 电表特有统计字段
    /**
     * 峰时用电量 - 仅电表
     */
    private BigDecimal peakConsumption;

    /**
     * 平时用电量 - 仅电表
     */
    private BigDecimal flatConsumption;

    /**
     * 谷时用电量 - 仅电表
     */
    private BigDecimal valleyConsumption;

    /**
     * 平均功率因数 - 仅电表
     */
    private BigDecimal avgPowerFactor;

    // 热表特有统计字段
    /**
     * 平均供水温度 - 仅热表
     */
    private BigDecimal avgSupplyTemp;

    /**
     * 平均回水温度 - 仅热表
     */
    private BigDecimal avgReturnTemp;

    /**
     * 平均温差 - 仅热表
     */
    private BigDecimal avgTempDiff;

    /**
     * 累计流量 - 仅热表
     */
    private BigDecimal totalFlow;

    // 气表特有统计字段
    /**
     * 平均压力 - 仅气表
     */
    private BigDecimal avgPressure;

    /**
     * 平均温度 - 仅气表
     */
    private BigDecimal avgTemperature;

    /**
     * 标况用气量 - 仅气表
     */
    private BigDecimal standardConsumption;
}