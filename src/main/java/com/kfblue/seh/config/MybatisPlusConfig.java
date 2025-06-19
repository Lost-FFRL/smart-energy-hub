package com.kfblue.seh.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import cn.dev33.satoken.stp.StpUtil;
import com.kfblue.seh.service.SysUserService;
import com.kfblue.seh.entity.SysUser;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置类
 */
@Configuration
public class MybatisPlusConfig {
    
    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
    
    /**
     * 自动填充处理器
     */
    @Component
    public static class MyMetaObjectHandler implements MetaObjectHandler, ApplicationContextAware {
        
        private ApplicationContext applicationContext;
        
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }
        
        /**
         * 获取当前用户名
         */
        private String getCurrentUsername() {
            try {
                if (StpUtil.isLogin()) {
                    // 延迟获取SysUserService，避免循环依赖
                    SysUserService sysUserService = applicationContext.getBean(SysUserService.class);
                    SysUser currentUser = sysUserService.getCurrentUser();
                    return currentUser != null ? currentUser.getUsername() : "system";
                }
            } catch (Exception e) {
                // 如果获取用户失败，返回默认值
            }
            return "system";
        }
        
        @Override
        public void insertFill(MetaObject metaObject) {
            LocalDateTime now = LocalDateTime.now();
            String currentUsername = getCurrentUsername();
            
            this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
            this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
            this.strictInsertFill(metaObject, "createdBy", String.class, currentUsername);
            this.strictInsertFill(metaObject, "updatedBy", String.class, currentUsername);
            this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
        }
        
        @Override
        public void updateFill(MetaObject metaObject) {
            String currentUsername = getCurrentUsername();
            
            this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            this.strictUpdateFill(metaObject, "updatedBy", String.class, currentUsername);
        }
    }
}