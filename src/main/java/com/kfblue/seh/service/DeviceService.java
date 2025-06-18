package com.kfblue.seh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.Device;
import com.kfblue.seh.mapper.DeviceMapper;
import com.kfblue.seh.vo.DeviceOnlineStatVO;
import com.kfblue.seh.vo.DeviceTypeStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService extends ServiceImpl<DeviceMapper, Device> {
    
    /**
     * 获取所有活跃设备（状态为正常且未删除）
     */
    public List<Device> getAllActiveDevices() {
        return this.lambdaQuery()
            .eq(Device::getStatus, 1) // 正常状态
            .eq(Device::getDeleted, 0) // 未删除
            .list();
    }
    
    /**
     * 获取在线设备列表
     */
    public List<Device> getOnlineDevices() {
        return this.lambdaQuery()
            .eq(Device::getStatus, 1) // 正常状态
            .eq(Device::getOnlineStatus, 1) // 在线
            .eq(Device::getDeleted, 0) // 未删除
            .list();
    }
    
    /**
     * 按设备类型统计设备数量
     */
    public List<DeviceTypeStatVO> countByDeviceType() {
        return baseMapper.countByDeviceType();
    }
    
    /**
     * 获取设备在线状态统计
     */
    public DeviceOnlineStatVO getOnlineStats() {
        return baseMapper.selectOnlineStats();
    }
    
    /**
     * 根据设备编码查询设备
     * @param deviceCode 设备编码
     * @return 设备信息
     */
    public Device getByDeviceCode(String deviceCode) {
        return this.lambdaQuery()
                .eq(Device::getDeviceCode, deviceCode)
                .eq(Device::getDeleted, 0)
                .one();
    }

    /**
     * 验证设备编码是否唯一
     *
     * @param deviceCode 设备编码
     * @param excludeId  排除的设备ID（用于编辑时排除自身）
     * @return true表示唯一，false表示重复
     */
    public boolean isDeviceCodeUnique(String deviceCode, Long excludeId) {
        var query = this.lambdaQuery()
                .eq(Device::getDeviceCode, deviceCode)
                .eq(Device::getDeleted, 0);
        
        // 如果有排除ID，则排除该设备
        if (excludeId != null) {
            query.ne(Device::getId, excludeId);
        }
        
        return query.count() == 0;
    }

    /**
     * 检查离线设备
     *
     * @param offlineThresholdMinutes 离线阈值（分钟）
     * @return 离线设备列表
     */
    public List<Device> checkOfflineDevices(Integer offlineThresholdMinutes) {
        // 计算离线时间阈值
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(offlineThresholdMinutes);
        
        // 查询最后在线时间早于阈值的设备
        return this.lambdaQuery()
                .eq(Device::getDeleted, 0)
                .lt(Device::getLastOnlineTime, thresholdTime)
                 .list();
     }

    /**
     * 根据区域ID查询设备列表
     *
     * @param regionId 区域ID
     * @return 设备列表
     */
    public List<Device> getByRegionId(Long regionId) {
        return this.lambdaQuery()
                .eq(Device::getRegionId, regionId)
                .eq(Device::getDeleted, 0)
                .list();
    }

    /**
     * 根据设备类型查询设备列表
     *
     * @param deviceType 设备类型
     * @return 设备列表
     */
    public List<Device> getByDeviceType(String deviceType) {
        return this.lambdaQuery()
                .eq(Device::getDeviceType, deviceType)
                .eq(Device::getDeleted, 0)
                .list();
    }

    /**
     * 根据在线状态查询设备列表
     *
     * @param onlineStatus 在线状态
     * @return 设备列表
     */
    public List<Device> getByOnlineStatus(Integer onlineStatus) {
        return this.lambdaQuery()
                .eq(Device::getOnlineStatus, onlineStatus)
                .eq(Device::getDeleted, 0)
                .list();
    }

    /**
     * 创建设备
     *
     * @param device 设备信息
     * @return 是否成功
     */
    public boolean createDevice(Device device) {
        return this.save(device);
    }

    /**
     * 更新设备
     *
     * @param device 设备信息
     * @return 是否成功
     */
    public boolean updateDevice(Device device) {
        return this.updateById(device);
    }

    /**
     * 删除设备（逻辑删除）
     *
     * @param id 设备ID
     * @return 是否成功
     */
    public boolean deleteDevice(Long id) {
        return this.removeById(id);
    }

    /**
     * 批量更新设备状态
     *
     * @param deviceIds 设备ID列表
     * @param status 状态
     * @return 是否成功
     */
    public boolean batchUpdateStatus(List<Long> deviceIds, Integer status) {
        return this.lambdaUpdate()
                .in(Device::getId, deviceIds)
                .set(Device::getStatus, status)
                .update();
    }

    /**
     * 更新设备在线状态
     *
     * @param id 设备ID
     * @param onlineStatus 在线状态
     * @param lastOnlineTime 最后在线时间
     * @return 是否成功
     */
    public boolean updateOnlineStatus(Long id, Integer onlineStatus, LocalDateTime lastOnlineTime) {
        return this.lambdaUpdate()
                .eq(Device::getId, id)
                .set(Device::getOnlineStatus, onlineStatus)
                .set(Device::getLastOnlineTime, lastOnlineTime)
                .update();
    }
 }