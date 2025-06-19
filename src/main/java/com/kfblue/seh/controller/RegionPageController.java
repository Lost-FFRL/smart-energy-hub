package com.kfblue.seh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 区域管理页面控制器
 *
 * @author system
 * @since 2025-06-17
 */
@Controller
@RequestMapping("/region-management")
public class RegionPageController {

    /**
     * 区域管理页面
     *
     * @return 区域管理页面模板
     */
    @GetMapping
    public String regionManagement() {
        return "region-management";
    }
}