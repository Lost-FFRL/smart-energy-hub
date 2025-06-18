package com.kfblue.seh.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RateUtils {

    /**
     * 计算增长率
     *
     * @param currentValue 当前值
     * @param baseValue    基准值（分母）
     * @return 增长率百分比，null表示无法计算
     */
    public static Double calculateGrowthRate(Double currentValue, Double baseValue) {
        // 处理null值
        if (currentValue == null) {
            currentValue = 0.0;
        }
        if (baseValue == null) {
            baseValue = 0.0;
        }

        // 处理分母为0的情况
        if (baseValue == 0.0) {
            if (currentValue == 0.0) {
                return 0.0;  // 0 vs 0 = 无变化
            } else {
                return null; // 有值 vs 0 = 无法计算百分比，前端显示为 "N/A"
            }
        }

        // 正常计算：(当前值 - 基准值) / 基准值 * 100
        double rate = ((currentValue - baseValue) / baseValue) * 100;

        // 保留2位小数
        return Math.round(rate * 100.0) / 100.0;
    }

    /**
     * 计算增长率，支持 BigDecimal 类型
     *
     * @param currentValue 当前值
     * @param baseValue    基准值（分母）
     * @return 增长率百分比，null表示无法计算
     */
    public static BigDecimal calculateGrowthRate(BigDecimal currentValue, BigDecimal baseValue) {
        // 处理 null 值
        if (currentValue == null) {
            currentValue = BigDecimal.ZERO;
        }
        if (baseValue == null) {
            baseValue = BigDecimal.ZERO;
        }

        // 处理分母为0的情况
        if (BigDecimal.ZERO.compareTo(baseValue) == 0) {
            if (BigDecimal.ZERO.compareTo(currentValue) == 0) {
                return BigDecimal.ZERO; // 0 vs 0 = 无变化
            } else {
                return null; // 有值 vs 0 = 无法计算百分比，前端显示为 "N/A"
            }
        }

        // 正常计算：(当前值 - 基准值) / 基准值 * 100
        BigDecimal rate = currentValue.subtract(baseValue)
                .divide(baseValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        // 保留2位小数
        return rate.setScale(2, RoundingMode.HALF_UP);
    }
}
