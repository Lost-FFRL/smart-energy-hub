package com.kfblue.seh.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.kfblue.seh.constants.ApiPaths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SA-Token 配置类
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    
    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定一条 match 规则
            SaRouter
                .match("/**")    // 拦截的 path 列表，可以写多个 */
                .notMatch("/")   // 排除掉的 path 列表，可以写多个
                .notMatch("/login")   // 排除登录页面
                .notMatch(ApiPaths.API_V0 + "/auth/login")   // 排除登录接口
                .notMatch(ApiPaths.API_V0 + "/auth/logout")  // 排除登出接口
                .notMatch("/static/**")       // 排除静态资源
                .notMatch("/css/**")
                .notMatch("/js/**")
                .notMatch("/images/**")
                .notMatch("/favicon.ico")
                .notMatch("/error")
                // 排除能耗相关查询接口（无需登录）
                .notMatch(ApiPaths.API_V0 + "/energy/**")
                // 排除测试接口
                .notMatch(ApiPaths.API_V0 + "/test/**")
                // 排除Swagger相关
                .notMatch("/swagger-ui/**")
                .notMatch("/v3/api-docs/**")
                .notMatch("/swagger-resources/**")
                .notMatch("/webjars/**")
                .check(r -> StpUtil.checkLogin());        // 要执行的校验动作，可以写完整的 lambda 表达式
        })).addPathPatterns("/**");
    }
}