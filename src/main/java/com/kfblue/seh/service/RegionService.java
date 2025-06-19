package com.kfblue.seh.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.dto.RegionDTO;
import com.kfblue.seh.entity.Region;
import com.kfblue.seh.mapper.DeviceMapper;
import com.kfblue.seh.mapper.RegionMapper;
import com.kfblue.seh.vo.RegionTreeVO;
import com.kfblue.seh.vo.RegionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 区域服务实现类
 *
 * @author system
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
public class RegionService extends ServiceImpl<RegionMapper, Region> {

    private final DeviceMapper deviceMapper;

    /**
     * 根据ID更新区域
     *
     * @param region 区域实体
     * @return 是否成功
     */
    public boolean updateById(Region region) {
        return baseMapper.updateById(region) > 0;
    }
    /**
     * 根据ID删除区域
     *
     * @param id 区域ID
     * @return 是否成功
     */
    public boolean removeById(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 区域类型映射
     */
    private static final Map<String, String> REGION_TYPE_MAP = Map.of(
            "office", "办公区域",
            "workshop", "车间区域",
            "dormitory", "宿舍区域",
            "other", "其他区域"
    );

    /**
     * 状态映射
     */
    private static final Map<Integer, String> STATUS_MAP = Map.of(
            0, "禁用",
            1, "启用"
    );

    public List<RegionVO> selectByLevel(Integer level) {
        level = null == level ? 0 : level;
        List<Region> regions = baseMapper.findByLevel(level);
        return regions.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    public Set<Long> selectChildrenIds(Long id) {
        Set<Long> result = new HashSet<>();
        Region region = baseMapper.findById(id);
        if (region != null) {
            result.add(id);
            collectChildrenIds(id, result);
        }
        return result;
    }

    /**
     * 根据ID获取区域
     *
     * @param id 区域ID
     * @return 区域信息
     */
    public RegionVO getById(Long id) {
        Region region = baseMapper.findById(id);
        return region != null ? convertToVO(region) : null;
    }

    /**
     * 获取区域树形结构
     *
     * @return 区域树
     */
    public List<RegionTreeVO> getRegionTree() {
        // 查询所有区域
        List<Region> allRegions = baseMapper.findAll();

        // 转换为TreeVO并设置额外信息
        List<RegionTreeVO> regionTreeVOs = allRegions.stream()
                .map(this::convertToTreeVO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(regionTreeVOs, null);
    }

    /**
     * 获取子区域列表
     *
     * @param parentId 父区域ID
     * @return 子区域列表
     */
    public List<RegionVO> getChildrenRegions(Long parentId) {
        List<Region> children = baseMapper.findByParentId(parentId);
        return children.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 创建区域
     *
     * @param regionDTO 区域信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createRegion(RegionDTO regionDTO) {
        // 检查区域编码是否重复
        Region existingRegion = baseMapper.findByRegionCode(regionDTO.getRegionCode());
        if (existingRegion != null) {
            throw new RuntimeException("区域编码已存在");
        }

        Region region = BeanUtil.copyProperties(regionDTO, Region.class);
        
        // 设置区域路径
        String regionPath = buildRegionPath(regionDTO.getParentId());
        region.setRegionPath(regionPath);
        
        return save(region);
    }

    /**
     * 更新区域
     *
     * @param id        区域ID
     * @param regionDTO 区域信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRegion(Long id, RegionDTO regionDTO) {
        Region existingRegion = super.getById(id);
        if (existingRegion == null) {
            throw new RuntimeException("区域不存在");
        }

        // 检查区域编码是否重复（排除自己）
        Region codeCheckRegion = baseMapper.findByRegionCode(regionDTO.getRegionCode());
        if (codeCheckRegion != null && !codeCheckRegion.getId().equals(id)) {
            throw new RuntimeException("区域编码已存在");
        }

        Region region = BeanUtil.copyProperties(regionDTO, Region.class);
        region.setId(id);
        
        // 更新区域路径
        String regionPath = buildRegionPath(regionDTO.getParentId());
        region.setRegionPath(regionPath);
        
        return updateById(region);
    }

    /**
     * 删除区域
     *
     * @param id 区域ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRegion(Long id) {
        // 检查是否有子区域
        List<Region> children = baseMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子区域，无法删除");
        }

        // 检查是否有关联设备
        Long deviceCount = deviceMapper.selectCount(
                new QueryWrapper<com.kfblue.seh.entity.Device>().eq("region_id", id)
        );
        if (deviceCount > 0) {
            throw new RuntimeException("存在关联设备，无法删除");
        }

        return removeById(id);
    }

    /**
     * 批量删除区域
     *
     * @param ids 区域ID列表
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteRegions(List<Long> ids) {
        for (Long id : ids) {
            deleteRegion(id);
        }
        return true;
    }

    /**
     * 更新区域状态
     *
     * @param id     区域ID
     * @param status 状态
     * @return 是否成功
     */
    public boolean updateRegionStatus(Long id, Integer status) {
        Region region = new Region();
        region.setId(id);
        region.setStatus(status);
        return updateById(region);
    }

    /**
     * 查询区域列表
     *
     * @param regionType 区域类型
     * @param status 状态
     * @param keyword 关键词
     * @return 区域列表
     */
    public List<RegionVO> getRegionList(String regionType, Integer status, String keyword) {
        List<Region> regions = baseMapper.findAll();
        
        return regions.stream()
                .filter(region -> {
                    if (StringUtils.hasText(regionType) && !regionType.equals(region.getRegionType())) {
                        return false;
                    }
                    if (status != null && !status.equals(region.getStatus())) {
                        return false;
                    }
                    if (StringUtils.hasText(keyword)) {
                        String lowerKeyword = keyword.toLowerCase();
                        return region.getRegionName().toLowerCase().contains(lowerKeyword) ||
                               region.getRegionCode().toLowerCase().contains(lowerKeyword);
                    }
                    return true;
                })
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取区域类型选项
     *
     * @return 区域类型列表
     */
    public List<String> getRegionTypes() {
        return new ArrayList<>(REGION_TYPE_MAP.keySet());
    }

    /**
     * 递归收集子节点
     *
     * @param parentId 父节点ID
     * @param result   存储结果的集合
     */
    private void collectChildrenIds(Long parentId, Set<Long> result) {
        List<Region> children = baseMapper.findByParentId(parentId);
        for (Region child : children) {
            result.add(child.getId());
            collectChildrenIds(child.getId(), result); // 递归查找下一级
        }
    }

    /**
     * 计算区域及其所有子区域的设备总数
     *
     * @param regionId 区域ID
     * @return 设备总数
     */
    private int countAllDevicesInRegion(Long regionId) {
        // 获取当前区域的设备数量
        int totalCount = baseMapper.countDevicesByRegionId(regionId);
        
        // 获取所有子区域ID（不包含当前区域）
        Set<Long> childrenIds = new HashSet<>();
        collectChildrenIds(regionId, childrenIds);
        
        // 累加所有子区域的设备数量
        for (Long childId : childrenIds) {
            totalCount += baseMapper.countDevicesByRegionId(childId);
        }
        
        return totalCount;
    }

    /**
     * 构建区域路径
     *
     * @param parentId 父区域ID
     * @return 区域路径
     */
    private String buildRegionPath(Long parentId) {
        if (parentId == null) {
            return "/";
        }
        
        Region parent = baseMapper.findById(parentId);
        if (parent == null) {
            return "/";
        }
        
        return parent.getRegionPath() + parentId + "/";
    }

    /**
     * 构建树形结构
     *
     * @param regions  区域列表
     * @param parentId 父区域ID
     * @return 树形结构
     */
    private List<RegionTreeVO> buildTree(List<RegionTreeVO> regions, Long parentId) {
        return regions.stream()
                .filter(region -> Objects.equals(region.getParentId(), parentId))
                .peek(region -> {
                    List<RegionTreeVO> children = buildTree(regions, region.getId());
                    region.setChildren(children);
                    region.setHasChildren(!children.isEmpty());
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换为TreeVO
     *
     * @param region 区域实体
     * @return TreeVO
     */
    private RegionTreeVO convertToTreeVO(Region region) {
        RegionTreeVO treeVO = new RegionTreeVO();
        treeVO.setId(region.getId());
        treeVO.setRegionCode(region.getRegionCode());
        treeVO.setRegionName(region.getRegionName());
        treeVO.setParentId(region.getParentId());
        treeVO.setRegionLevel(region.getRegionLevel());
        treeVO.setRegionPath(region.getRegionPath());
        treeVO.setAreaSize(region.getAreaSize() != null ? BigDecimal.valueOf(region.getAreaSize()) : null);
        treeVO.setRegionType(region.getRegionType());
        treeVO.setStatus(region.getStatus());
        treeVO.setRemark(region.getRemark());
        
        // 设置显示标签
        treeVO.setRegionTypeLabel(REGION_TYPE_MAP.getOrDefault(region.getRegionType(), region.getRegionType()));
        treeVO.setStatusLabel(STATUS_MAP.getOrDefault(region.getStatus(), "未知"));
        
        // 设置设备数量（包含所有子区域的设备）
        int deviceCount = countAllDevicesInRegion(region.getId());
        treeVO.setDeviceCount(deviceCount);
        
        return treeVO;
    }

    /**
     * 转换为VO
     *
     * @param region 区域实体
     * @return VO
     */
    private RegionVO convertToVO(Region region) {
        RegionVO vo = new RegionVO();
        vo.setId(region.getId());
        vo.setRegionCode(region.getRegionCode());
        vo.setRegionName(region.getRegionName());
        vo.setParentId(region.getParentId());
        vo.setRegionLevel(region.getRegionLevel());
        vo.setRegionPath(region.getRegionPath());
        vo.setAreaSize(region.getAreaSize() != null ? BigDecimal.valueOf(region.getAreaSize()) : null);
        vo.setRegionType(region.getRegionType());
        vo.setStatus(region.getStatus());
        vo.setRemark(region.getRemark());
        vo.setCreatedBy(region.getCreatedBy());
        vo.setCreatedAt(region.getCreatedAt());
        vo.setUpdatedBy(region.getUpdatedBy());
        vo.setUpdatedAt(region.getUpdatedAt());
        
        // 设置父区域名称
        if (region.getParentId() != null) {
            Region parent = baseMapper.findById(region.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getRegionName());
            }
        }
        
        // 设置显示标签
        vo.setRegionTypeLabel(REGION_TYPE_MAP.getOrDefault(region.getRegionType(), region.getRegionType()));
        vo.setStatusLabel(STATUS_MAP.getOrDefault(region.getStatus(), "未知"));
        
        // 设置设备数量（包含所有子区域的设备）
        int deviceCount = countAllDevicesInRegion(region.getId());
        vo.setDeviceCount(deviceCount);
        
        // 检查是否有子区域
        int childrenCount = baseMapper.countByParentId(region.getId());
        vo.setHasChildren(childrenCount > 0);
        
        return vo;
    }
}