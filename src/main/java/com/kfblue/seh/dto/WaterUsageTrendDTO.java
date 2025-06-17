package com.kfblue.seh.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WaterUsageTrendDTO {
    private Long deviceId;
    private String deviceName;
    private List<WaterUsagePoint> points;

    @Data
    public static class WaterUsagePoint {
        private LocalDateTime time;
        private Double value;
        private Double yesterdayValue;
    }
}