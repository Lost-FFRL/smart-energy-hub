package com.kfblue.seh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 设备统计数据传输对象
 * 
 * @author system
 * @since 2025-01-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatisticsDTO {
    
    /**
     * 设备总数
     */
    private Integer total;
    
    /**
     * 在线设备数量
     */
    private Integer onlineCnt;
    
    /**
     * 离线设备数量
     */
    private Integer offlineCnt;
    
    /**
     * 工作设备数量
     */
    private Integer workCnt;
    
    /**
     * 告警设备数量
     */
    private Integer alarmCnt;
}