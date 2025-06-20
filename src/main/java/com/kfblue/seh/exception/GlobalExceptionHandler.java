package com.kfblue.seh.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.kfblue.seh.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 全局异常处理器
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理SA-Token登录检测异常
     * 当用户未登录访问需要登录的页面时，直接重定向到首页
     */
    @ExceptionHandler(NotLoginException.class)
    public Object handleNotLoginException(NotLoginException e, HttpServletRequest request, HttpServletResponse response) {
        log.warn("用户未登录访问受保护页面: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        
        // 判断是否是AJAX请求
        String requestedWith = request.getHeader("X-Requested-With");
        String contentType = request.getHeader("Content-Type");
        String accept = request.getHeader("Accept");
        
        // 判断是否为API请求（AJAX、JSON请求等）
        boolean isApiRequest = "XMLHttpRequest".equals(requestedWith) 
                || (contentType != null && contentType.contains("application/json"))
                || (accept != null && accept.contains("application/json"))
                || request.getRequestURI().startsWith("/api/");
        
        if (isApiRequest) {
            // API请求，返回JSON响应
            return ResponseEntity.status(401).body(Result.error(401, "用户未登录，请先登录"));
        } else {
            // 普通页面请求，重定向到首页
            ModelAndView mv = new ModelAndView();
            mv.setViewName("redirect:/");
            return mv;
        }
    }

    /**
     * 处理业务异常（RuntimeException）
     */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.warn("业务异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI());
        
        // 判断是否为API请求
        String requestedWith = request.getHeader("X-Requested-With");
        String contentType = request.getHeader("Content-Type");
        String accept = request.getHeader("Accept");
        
        boolean isApiRequest = "XMLHttpRequest".equals(requestedWith) 
                || (contentType != null && contentType.contains("application/json"))
                || (accept != null && accept.contains("application/json"))
                || request.getRequestURI().startsWith("/api/");
        
        if (isApiRequest) {
            // API请求，返回JSON响应
            return ResponseEntity.ok(Result.error(e.getMessage()));
        } else {
            // 普通页面请求，重定向到首页
            ModelAndView mv = new ModelAndView();
            mv.setViewName("redirect:/");
            return mv;
        }
    }

    /**
     * 处理其他未捕获的异常
     * 避免显示默认的Whitelabel Error Page
     */
    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        
        // 检查是否是静态资源请求，如果是则不处理，让Spring Boot默认机制处理
        if (isStaticResourceRequest(requestURI)) {
            log.debug("静态资源请求异常，不进行全局处理: {}", requestURI);
            // 返回null让Spring Boot的默认错误处理机制处理
            throw new RuntimeException(e);
        }
        
        log.error("系统异常: {}, 请求路径: {}", e.getMessage(), requestURI, e);
        
        // 判断是否为API请求
        String requestedWith = request.getHeader("X-Requested-With");
        String contentType = request.getHeader("Content-Type");
        String accept = request.getHeader("Accept");
        
        boolean isApiRequest = "XMLHttpRequest".equals(requestedWith) 
                || (contentType != null && contentType.contains("application/json"))
                || (accept != null && accept.contains("application/json"))
                || requestURI.startsWith("/api/");
        
        if (isApiRequest) {
            // API请求，返回JSON响应
            return ResponseEntity.ok(Result.error("系统异常，请稍后重试"));
        } else {
            // 普通页面请求，重定向到首页
            ModelAndView mv = new ModelAndView();
            mv.setViewName("redirect:/");
            return mv;
        }
    }
    
    /**
     * 判断是否是静态资源请求
     * @param requestURI 请求URI
     * @return 是否是静态资源请求
     */
    private boolean isStaticResourceRequest(String requestURI) {
        if (requestURI == null) {
            return false;
        }
        
        // 常见的静态资源路径和文件扩展名
        String[] staticPaths = {
            "/favicon.ico", "/robots.txt", "/sitemap.xml",
            "/static/", "/css/", "/js/", "/images/", "/img/", "/fonts/"
        };
        
        String[] staticExtensions = {
            ".css", ".js", ".ico", ".png", ".jpg", ".jpeg", ".gif", ".svg",
            ".woff", ".woff2", ".ttf", ".eot", ".map", ".webp"
        };
        
        String lowerURI = requestURI.toLowerCase();
        
        // 检查是否匹配静态资源路径
        for (String path : staticPaths) {
            if (lowerURI.startsWith(path)) {
                return true;
            }
        }
        
        // 检查是否匹配静态资源扩展名
        for (String ext : staticExtensions) {
            if (lowerURI.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }
}