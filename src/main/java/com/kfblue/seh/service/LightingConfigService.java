package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfblue.seh.dto.LightingConfigDTO;
import com.kfblue.seh.dto.TimePeriodDTO;
import com.kfblue.seh.entity.LightingConfig;
import com.kfblue.seh.mapper.LightingConfigMapper;
import com.kfblue.seh.vo.LightingConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 照明配置服务实现类
 *
 * @author system
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LightingConfigService extends ServiceImpl<LightingConfigMapper, LightingConfig>{

    private final ObjectMapper objectMapper;

    /**
     * 分页查询照明配置
     *
     * @param current 当前页
     * @param size 每页大小
     * @param configName 配置名称（可选）
     * @param controlMode 控制模式（可选）
     * @param enabled 启用状态（可选）
     * @return 分页结果
     */
    public IPage<LightingConfigVO> getConfigPage(long current, long size, String configName, String controlMode, Boolean enabled) {
        Page<LightingConfig> page = new Page<>(current, size);
        QueryWrapper<LightingConfig> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(configName)) {
            queryWrapper.like("config_name", configName);
        }
        if (StringUtils.hasText(controlMode)) {
            queryWrapper.eq("control_mode", controlMode);
        }
        if (enabled != null) {
            queryWrapper.eq("is_enabled", enabled ? 1 : 0);
        }
        
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderByDesc("created_at");
        
        IPage<LightingConfig> entityPage = this.page(page, queryWrapper);
        
        // 转换为VO
        IPage<LightingConfigVO> voPage = new Page<>(current, size, entityPage.getTotal());
        List<LightingConfigVO> voList = entityPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    /**
     * 根据ID查询配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    public LightingConfigVO getConfigById(Long id) {
        LightingConfig entity = this.getById(id);
        return entity != null ? convertToVO(entity) : null;
    }

    /**
     * 保存配置
     *
     * @param dto 配置DTO
     * @return 是否成功
     */
    public boolean saveConfig(LightingConfigDTO dto) {
        LightingConfig entity = convertToEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return this.save(entity);
    }

    /**
     * 更新配置
     *
     * @param id 配置ID
     * @param dto 配置DTO
     * @return 是否成功
     */
    public boolean updateConfig(Long id, LightingConfigDTO dto) {
        LightingConfig entity = convertToEntity(dto);
        entity.setId(id);
        entity.setUpdatedAt(LocalDateTime.now());
        return this.updateById(entity);
    }

    /**
     * 删除配置（逻辑删除）
     *
     * @param id 配置ID
     * @return 是否成功
     */
    public boolean deleteConfig(Long id) {
        LightingConfig entity = new LightingConfig();
        entity.setId(id);
        entity.setDeleted(1);
        entity.setUpdatedAt(LocalDateTime.now());
        return this.updateById(entity);
    }

    /**
     * 更新配置状态
     *
     * @param id 配置ID
     * @param enabled 启用状态
     * @return 是否成功
     */
    public boolean updateConfigStatus(Long id, boolean enabled) {
        LightingConfig entity = new LightingConfig();
        entity.setId(id);
        entity.setIsEnabled(enabled ? 1 : 0);
        entity.setUpdatedAt(LocalDateTime.now());
        return this.updateById(entity);
    }

    /**
     * 查询启用的配置列表
     *
     * @return 启用的配置列表
     */
    public List<LightingConfigVO> getEnabledConfigs() {
        List<LightingConfig> entities = baseMapper.selectEnabledConfigs();
        return entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据控制模式查询配置列表
     *
     * @param controlMode 控制模式
     * @return 配置列表
     */
    public List<LightingConfigVO> getConfigsByControlMode(String controlMode) {
        List<LightingConfig> entities = baseMapper.selectByControlMode(controlMode);
        return entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据区域ID查询配置列表
     *
     * @param regionId 区域ID
     * @return 配置列表
     */
    public List<LightingConfigVO> getConfigsByRegionId(Long regionId) {
        List<LightingConfig> entities = baseMapper.selectByRegionId(regionId);
        return entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据设备类型查询配置列表
     *
     * @param deviceType 设备类型
     * @return 配置列表
     */
    public List<LightingConfigVO> getConfigsByDeviceType(String deviceType) {
        List<LightingConfig> entities = baseMapper.selectByDeviceType(deviceType);
        return entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据设备编码查询相关配置
     *
     * @param deviceCode 设备编码
     * @return 相关配置列表
     */
    public List<LightingConfigVO> getConfigsByDeviceCode(String deviceCode) {
        List<LightingConfig> entities = baseMapper.selectByDeviceCode(deviceCode);
        return entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 实体转换为VO（重点：JSON字符串转换为集合对象）
     *
     * @param entity 实体对象
     * @return VO对象
     */
    private LightingConfigVO convertToVO(LightingConfig entity) {
        LightingConfigVO vo = new LightingConfigVO();
        BeanUtils.copyProperties(entity, vo);
        // 处理时段配置JSON转换为集合对象
        if (StringUtils.hasText(entity.getTimePeriods())) {
            try {
                List<TimePeriodDTO> timePeriods = objectMapper.readValue(
                    entity.getTimePeriods(), 
                    new TypeReference<>() {}
                );
                vo.setTimePeriods(timePeriods);
                log.debug("成功解析时段配置JSON: {} -> {}", entity.getTimePeriods(), timePeriods.size());
            } catch (JsonProcessingException e) {
                log.error("解析时段配置JSON失败: {}", entity.getTimePeriods(), e);
                vo.setTimePeriods(new ArrayList<>());
            }
        } else {
            vo.setTimePeriods(new ArrayList<>());
        }
        return vo;
    }

    /**
     * DTO转换为实体（重点：集合对象转换为JSON字符串）
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    private LightingConfig convertToEntity(LightingConfigDTO dto) {
        LightingConfig entity = new LightingConfig();
        BeanUtils.copyProperties(dto, entity);
        
        // 处理时段配置集合转换为JSON字符串
        if (dto.getTimePeriods() != null && !dto.getTimePeriods().isEmpty()) {
            try {
                String timePeriodsJson = objectMapper.writeValueAsString(dto.getTimePeriods());
                entity.setTimePeriods(timePeriodsJson);
                log.debug("成功序列化时段配置: {} -> {}", dto.getTimePeriods().size(), timePeriodsJson);
            } catch (JsonProcessingException e) {
                log.error("序列化时段配置失败", e);
                entity.setTimePeriods("[]");
            }
        } else {
            entity.setTimePeriods("[]");
        }
        
        // 处理目标设备集合转换为JSON字符串
        if (dto.getTargetDevices() != null && !dto.getTargetDevices().isEmpty()) {
            try {
                String targetDevicesJson = objectMapper.writeValueAsString(dto.getTargetDevices());
                entity.setTargetDevices(targetDevicesJson);
                log.debug("成功序列化目标设备: {} -> {}", dto.getTargetDevices().size(), targetDevicesJson);
            } catch (JsonProcessingException e) {
                log.error("序列化目标设备失败", e);
                entity.setTargetDevices("[]");
            }
        } else {
            entity.setTargetDevices("[]");
        }
        
        return entity;
    }

    /**
     * 批量查询配置（支持多条件组合）
     *
     * @param controlMode 控制模式
     * @param regionId 区域ID
     * @param deviceType 设备类型
     * @param enabled 启用状态
     * @return 配置列表
     */
    public List<LightingConfigVO> getConfigsByConditions(String controlMode, Long regionId, String deviceType, Boolean enabled) {
        QueryWrapper<LightingConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        
        if (StringUtils.hasText(controlMode)) {
            queryWrapper.eq("control_mode", controlMode);
        }
        if (regionId != null) {
            queryWrapper.eq("region_id", regionId);
        }
        if (StringUtils.hasText(deviceType)) {
            queryWrapper.eq("device_type", deviceType);
        }
        if (enabled != null) {
            queryWrapper.eq("is_enabled", enabled ? 1 : 0);
        }
        
        queryWrapper.orderByDesc("created_at");
        
        List<LightingConfig> entities = this.list(queryWrapper);
        return entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 统计查询方法
     */
    public long countByControlMode(String controlMode) {
        QueryWrapper<LightingConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                   .eq("control_mode", controlMode);
        return this.count(queryWrapper);
    }

    public long countEnabledConfigs() {
        QueryWrapper<LightingConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                   .eq("is_enabled", 1);
        return this.count(queryWrapper);
    }
}