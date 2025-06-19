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
     * 处理其他未捕获的异常
     * 避免显示默认的Whitelabel Error Page
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        
        // 重定向到首页，避免显示错误页面
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/");
        return mv;
    }
}