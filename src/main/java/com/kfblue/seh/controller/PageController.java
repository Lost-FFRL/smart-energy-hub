package com.kfblue.seh.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器
 */
@Controller
public class PageController {
    
    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model) {
        // 检查是否已登录
        if (StpUtil.isLogin()) {
            // 已登录，跳转到控制台
            return "redirect:/dashboard";
        } else {
            // 未登录，显示首页
            return "index";
        }
    }
    
    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login(Model model) {
        // 如果已经登录，直接跳转到控制台
        if (StpUtil.isLogin()) {
            return "redirect:/dashboard";
        }
        return "login";
    }
    
    /**
     * 控制台页面
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 检查登录状态
        StpUtil.checkLogin();
        
        // 获取当前用户信息
        Long userId = StpUtil.getLoginIdAsLong();
        model.addAttribute("userId", userId);
        
        return "dashboard";
    }
    
    /**
     * 设备管理页面
     */
    @GetMapping("/admin/device")
    public String deviceManagement(Model model) {
        StpUtil.checkLogin();
        return "admin/device/list";
    }
    
    /**
     * 区域管理页面
     */
    @GetMapping("/admin/area")
    public String areaManagement(Model model) {
        StpUtil.checkLogin();
        return "admin/area/list";
    }
    
    /**
     * 系统配置页面
     */
    @GetMapping("/admin/system")
    public String systemConfig(Model model) {
        StpUtil.checkLogin();
        return "admin/system/config";
    }
}