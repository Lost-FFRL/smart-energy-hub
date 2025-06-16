package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kfblue.seh.entity.Demo;

/**
 * Demo服务接口
 */
public interface DemoService extends IService<Demo> {
    
    /**
     * 分页查询Demo
     * @param page 分页参数
     * @param name 名称(可选)
     * @param status 状态(可选)
     * @return 分页结果
     */
    IPage<Demo> page(Page<Demo> page, String name, Integer status);
    
    /**
     * 根据ID获取Demo
     * @param id ID
     * @return Demo对象
     */
    Demo getById(Long id);
    
    /**
     * 保存Demo
     * @param demo Demo对象
     * @return 是否成功
     */
    boolean save(Demo demo);
    
    /**
     * 更新Demo
     * @param demo Demo对象
     * @return 是否成功
     */
    boolean update(Demo demo);
    
    /**
     * 删除Demo
     * @param id ID
     * @return 是否成功
     */
    boolean delete(Long id);
}