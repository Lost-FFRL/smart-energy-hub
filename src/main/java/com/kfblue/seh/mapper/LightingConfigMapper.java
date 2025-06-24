package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.LightingConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 照明配置Mapper接口
 * 
 * @author system
 * @since 2025-01-27
 */
@Mapper
public interface LightingConfigMapper extends BaseMapper<LightingConfig> {

    /**
     * 根据控制模式查询配置列表
     * 
     * @param controlMode 控制模式
     * @return 配置列表
     */
    List<LightingConfig> selectByControlMode(@Param("controlMode") String controlMode);

    /**
     * 根据区域ID查询配置列表
     * 
     * @param regionId 区域ID
     * @return 配置列表
     */
    List<LightingConfig> selectByRegionId(@Param("regionId") Long regionId);

    /**
     * 根据设备类型查询配置列表
     * 
     * @param deviceType 设备类型
     * @return 配置列表
     */
    List<LightingConfig> selectByDeviceType(@Param("deviceType") String deviceType);

    /**
     * 查询启用的配置列表
     * 
     * @return 启用的配置列表
     */
    List<LightingConfig> selectEnabledConfigs();

    /**
     * 根据设备编码查询相关配置
     * 
     * @param deviceCode 设备编码
     * @return 相关配置列表
     */
    @org.apache.ibatis.annotations.Select("""
        SELECT * FROM lighting_config 
        WHERE deleted = 0 
        AND JSON_CONTAINS(target_devices, JSON_QUOTE(#{deviceCode}))
        """)
    List<LightingConfig> selectByDeviceCode(@Param("deviceCode") String deviceCode);
}