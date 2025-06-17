package com.kfblue.seh.dto;

import lombok.Data;

@Data
public class ElectricityComparisonDTO {
    private Long deviceId;
    private String deviceName;
    private Double currentHourUsage;
    private Double previousHourUsage;
    private Double samePeriodLastYearUsage;
    private Double hourToHourRate;
    private Double yearOverYearRate;
}
