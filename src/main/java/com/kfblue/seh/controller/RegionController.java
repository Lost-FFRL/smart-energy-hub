package com.kfblue.seh.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.kfblue.seh.common.Result;
import com.kfblue.seh.constants.ApiPaths;
import com.kfblue.seh.dto.RegionDTO;
import com.kfblue.seh.service.RegionService;
import com.kfblue.seh.vo.RegionTreeVO;
import com.kfblue.seh.vo.RegionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域管理控制器
 *
 * @author system
 * @since 2025-01-20
 */
@RestController
@RequestMapping(ApiPaths.API_V0 + "/regions")
@RequiredArgsConstructor
@SaCheckLogin
public class RegionController {

    private final RegionService regionService;

    /**
     * 获取区域树形结构
     *
     * @return 区域树
     */
    @GetMapping("/tree")
    public Result<List<RegionTreeVO>> getRegionTree() {
        List<RegionTreeVO> tree = regionService.getRegionTree();
        return Result.success(tree);
    }

    /**
     * 分页查询区域列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param regionType 区域类型
     * @param status 状态
     * @param keyword 关键词
     * @return 分页结果
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getRegionPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String regionType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        
        List<RegionVO> regions = regionService.getRegionList(regionType, status, keyword);
        
        // 简单的内存分页
        int start = page * size;
        int end = Math.min(start + size, regions.size());
        List<RegionVO> pageContent = regions.subList(start, end);
        
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("content", pageContent);
        pageInfo.put("totalElements", regions.size());
        pageInfo.put("totalPages", (int) Math.ceil((double) regions.size() / size));
        pageInfo.put("number", page);
        pageInfo.put("size", size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", pageInfo);
        response.put("msg", "查询成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取区域详情
     *
     * @param id 区域ID
     * @return 区域详情
     */
    @GetMapping("/{id}")
    public Result<RegionVO> getRegionById(@PathVariable Long id) {
        RegionVO regionVO = regionService.getById(id);
        if (regionVO == null) {
            return Result.error("区域不存在");
        }
        return Result.success(regionVO);
    }

    /**
     * 新增区域
     *
     * @param regionDTO 区域信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> createRegion(@Valid @RequestBody RegionDTO regionDTO) {
        boolean success = regionService.createRegion(regionDTO);
        return success ? Result.success() : Result.error("新增区域失败");
    }

    /**
     * 更新区域
     *
     * @param id        区域ID
     * @param regionDTO 区域信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> updateRegion(@PathVariable Long id, @Valid @RequestBody RegionDTO regionDTO) {
        boolean success = regionService.updateRegion(id, regionDTO);
        return success ? Result.success() : Result.error("更新区域失败");
    }

    /**
     * 删除区域
     *
     * @param id 区域ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRegion(@PathVariable Long id) {
        boolean success = regionService.deleteRegion(id);
        return success ? Result.success() : Result.error("删除区域失败");
    }

    /**
     * 批量删除区域
     *
     * @param ids 区域ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteRegions(@RequestBody List<Long> ids) {
        boolean success = regionService.batchDeleteRegions(ids);
        return success ? Result.success() : Result.error("批量删除区域失败");
    }

    /**
     * 获取子区域列表
     *
     * @param parentId 父区域ID
     * @return 子区域列表
     */
    @GetMapping("/children/{parentId}")
    public Result<List<RegionVO>> getChildrenRegions(@PathVariable Long parentId) {
        List<RegionVO> children = regionService.getChildrenRegions(parentId);
        return Result.success(children);
    }

    /**
     * 启用/禁用区域
     *
     * @param id     区域ID
     * @param status 状态(0:禁用,1:启用)
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateRegionStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = regionService.updateRegionStatus(id, status);
        return success ? Result.success() : Result.error("更新区域状态失败");
    }

    /**
     * 获取区域类型选项
     *
     * @return 区域类型列表
     */
    @GetMapping("/types")
    public Result<List<String>> getRegionTypes() {
        List<String> types = regionService.getRegionTypes();
        return Result.success(types);
    }
}