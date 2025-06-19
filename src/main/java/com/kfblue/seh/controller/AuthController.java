package com.kfblue.seh.controller;

import cn.hutool.core.util.StrUtil;
import com.kfblue.seh.common.Result;
import com.kfblue.seh.dto.LoginDTO;
import com.kfblue.seh.service.SysUserService;
import com.kfblue.seh.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、登出等认证相关接口")
public class AuthController {
    
    private final SysUserService sysUserService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            String clientIp = getClientIp(request);
            LoginVO loginVO = sysUserService.login(loginDTO, clientIp);
            return Result.success(loginVO);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录")
    public Result<Void> logout() {
        try {
            boolean success = sysUserService.logout();
            if (success) {
                return Result.success();
            } else {
                return Result.error("登出失败");
            }
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String xfor = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotEmpty(xfor) && !"unKnown".equalsIgnoreCase(xfor)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = xfor.indexOf(",");
            if (index != -1) {
                return xfor.substring(0, index);
            } else {
                return xfor;
            }
        }
        xfor = xip;
        if (StrUtil.isNotEmpty(xfor) && !"unKnown".equalsIgnoreCase(xfor)) {
            return xfor;
        }
        if (StrUtil.isBlank(xfor) || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(xfor) || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(xfor) || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(xfor) || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(xfor) || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getRemoteAddr();
        }
        return xfor;
    }
}