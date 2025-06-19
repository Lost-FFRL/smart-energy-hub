package com.kfblue.seh.constants;

/**
 * API路径常量
 * 统一管理所有后端接口的路径前缀
 * 当前版本：v0，固定前缀 /api 后面直接接业务模块
 */
public class ApiPaths {

    /**
     * API基础路径前缀
     * 用于区分后端API接口与前端页面请求
     */
    public static final String API_PREFIX = "/api";

    /**
     * 当前API版本前缀 (v0)
     * v0版本：/api 后面直接接业务模块
     */
    public static final String API = API_PREFIX;

    /**
     * 私有构造函数，防止实例化
     */
    private ApiPaths() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}