package com.kfblue.seh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * DevTools 热更新配置
 * 仅在开发环境生效
 * DevTools 会自动配置热更新功能，无需额外配置
 */
@Configuration
@Profile("!prod") // 非生产环境才启用
public class DevToolsConfig {
    
    // DevTools 会自动处理热更新配置
    // 排除规则已在 application.properties 中配置
    
}