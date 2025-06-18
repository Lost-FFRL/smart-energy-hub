package com.kfblue.seh.util;

import com.kfblue.seh.constant.DateTypeConsts;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {
    /**
     * 获取给定日期在指定时间维度下的开始时间
     *
     * @param date      给定的日期
     * @param dimension 时间维度: "month", "quarter", "year"
     * @return 开始时间
     */
    public static LocalDate getStartTime(LocalDate date, String dimension) {
        return switch (dimension) {
            case DateTypeConsts.MONTH  -> date.with(TemporalAdjusters.firstDayOfMonth());
            case DateTypeConsts.QUARTER -> {
                Month firstMonthOfQuarter = Month.of((((date.getMonth().getValue() - 1) / 3) * 3) + 1);
                yield LocalDate.of(date.getYear(), firstMonthOfQuarter, 1);
            }
            case DateTypeConsts.YEAR  -> LocalDate.of(date.getYear(), Month.JANUARY, 1);
            default -> throw new IllegalArgumentException("Invalid time dimension: " + dimension);
        };
    }

    /**
     * 获取给定日期在指定时间维度下的结束时间
     *
     * @param date      给定的日期
     * @param dimension 时间维度: "month", "quarter", "year"
     * @return 结束时间
     */
    public static LocalDate getEndTime(LocalDate date, String dimension) {
        return switch (dimension) {
            case DateTypeConsts.MONTH -> date.with(TemporalAdjusters.lastDayOfMonth());
            case DateTypeConsts.QUARTER -> {
                Month lastMonthOfQuarter = Month.of((((date.getMonth().getValue() - 1) / 3) * 3) + 3);
                yield LocalDate.of(date.getYear(), lastMonthOfQuarter, 1).with(TemporalAdjusters.lastDayOfMonth());
            }
            case DateTypeConsts.YEAR -> LocalDate.of(date.getYear(), Month.DECEMBER, 31);
            default -> throw new IllegalArgumentException("Invalid time dimension: " + dimension);
        };
    }
}