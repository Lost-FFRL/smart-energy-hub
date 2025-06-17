package com.kfblue.seh.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HourlyRainfallDTO {
    private LocalDateTime timestamp;
    private Double rainfall;
    private Double comparisonWithPreviousDay; // 同比(%)
    private Double comparisonWithPreviousPeriod; // 环比(%)
    private String unit = "mm";
}