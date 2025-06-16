package com.kfblue.seh.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfblue.seh.common.Result;
import com.kfblue.seh.entity.Demo;
import com.kfblue.seh.service.DemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Demo控制器
 */
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {
    
    private final DemoService demoService;
    
    /**
     * 分页查询Demo列表
     * @param current 当前页
     * @param size 每页大小
     * @param name 名称(可选)
     * @param status 状态(可选)
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<IPage<Demo>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        Page<Demo> page = new Page<>(current, size);
        IPage<Demo> result = demoService.page(page, name, status);
        return Result.success(result);
    }
    
    /**
     * 根据ID获取Demo详情
     * @param id ID
     * @return Demo对象
     */
    @GetMapping("/{id}")
    public Result<Demo> getById(@PathVariable Long id) {
        Demo demo = demoService.getById(id);
        if (demo == null) {
            return Result.error("数据不存在");
        }
        return Result.success(demo);
    }
    
    /**
     * 新增Demo
     * @param demo Demo对象
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> save(@RequestBody Demo demo) {
        boolean success = demoService.save(demo);
        return success ? Result.success("新增成功", null) : Result.error("新增失败");
    }
    
    /**
     * 更新Demo
     * @param demo Demo对象
     * @return 操作结果
     */
    @PutMapping
    public Result<Void> update(@RequestBody Demo demo) {
        boolean success = demoService.update(demo);
        return success ? Result.success("更新成功", null) : Result.error("更新失败");
    }
    
    /**
     * 删除Demo
     * @param id ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = demoService.delete(id);
        return success ? Result.success("删除成功", null) : Result.error("删除失败");
    }
}