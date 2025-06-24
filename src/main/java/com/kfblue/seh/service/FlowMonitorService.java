package com.kfblue.seh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.FlowMonitor;
import com.kfblue.seh.mapper.FlowMonitorMapper;
import com.kfblue.seh.vo.SimpleFlowDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流量监控服务类
 * 
 * @author system
 * @since 2025-06-24
 */
@Service
@RequiredArgsConstructor
public class FlowMonitorService extends ServiceImpl<FlowMonitorMapper, FlowMonitor> {

    /**
     * 根据设备ID和时间范围查询流量数据
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 流量监控数据列表
     */
    public List<FlowMonitor> getByDeviceIdAndTimeRange(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectByDeviceIdAndTimeRange(deviceId, startTime, endTime);
    }

    /**
     * 获取设备最新流量数据
     * 
     * @param deviceId 设备ID
     * @return 最新流量数据
     */
    public FlowMonitor getLatestByDeviceId(Long deviceId) {
        return baseMapper.selectLatestByDeviceId(deviceId);
    }

    /**
     * 获取小时流量统计数据
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 小时流量统计列表
     */
    public List<SimpleFlowDataVO> getHourlyFlowStatistics(Long deviceId,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        return baseMapper.getHourlyFlowStatistics(deviceId, startTime, endTime);
    }

    /**
     * 保存流量监控数据
     * 
     * @param flowMonitor 流量监控数据
     * @return 是否成功
     */
    public boolean saveFlowData(FlowMonitor flowMonitor) {
        // 设置监测时间为当前时间
        if (flowMonitor.getMonitorTime() == null) {
            flowMonitor.setMonitorTime(LocalDateTime.now());
        }
        
        // 设置默认数据状态为正常
        if (flowMonitor.getDataStatus() == null) {
            flowMonitor.setDataStatus(1);
        }
        
        // 设置默认报警状态为正常
        if (flowMonitor.getAlarmStatus() == null) {
            flowMonitor.setAlarmStatus(0);
        }
        
        return this.save(flowMonitor);
    }

    /**
     * 批量保存流量监控数据
     * 
     * @param flowMonitors 流量监控数据列表
     * @return 是否成功
     */
    public boolean batchSaveFlowData(List<FlowMonitor> flowMonitors) {
        if (flowMonitors == null || flowMonitors.isEmpty()) {
            return false;
        }
        
        // 设置默认值
        LocalDateTime now = LocalDateTime.now();
        for (FlowMonitor flowMonitor : flowMonitors) {
            if (flowMonitor.getMonitorTime() == null) {
                flowMonitor.setMonitorTime(now);
            }
            if (flowMonitor.getDataStatus() == null) {
                flowMonitor.setDataStatus(1);
            }
            if (flowMonitor.getAlarmStatus() == null) {
                flowMonitor.setAlarmStatus(0);
            }
        }
        
        return this.saveBatch(flowMonitors);
    }

}