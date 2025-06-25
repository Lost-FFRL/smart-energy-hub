package com.kfblue.seh.vo;

import lombok.Data;

/**
 * 设备统计信息VO
 * 
 * @author system
 * @since 2025-01-27
 */
@Data
public class DeviceStatisticsVO {
    
    /**
     * 设备总数
     */
    private Long totalCount;
    
    /**
     * 在线设备数
     */
    private Long onlineCount;
    
    /**
     * 离线设备数
     */
    private Long offlineCount;
    
    /**
     * 工作设备数
     */
    private Long workingCount;
    
    /**
     * 报警设备数
     */
    private Long alarmCount;
    
    /**
     * 在线率（%）
     */
    private Double onlineRate;
    
    /**
     * 离线率（%）
     */
    private Double offlineRate;
    
    /**
     * 报警率（%）
     */
    private Double alarmRate;
}