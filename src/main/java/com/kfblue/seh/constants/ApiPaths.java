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
    public static final String API_V0 = API_PREFIX;

    // ==================== 业务模块路径 ====================


    /**
     * 区域管理接口路径
     */
    public static final String REGIONS = API_V0 + "/regions";

    /**
     * 用户管理接口路径
     */
    public static final String USERS = API_V0 + "/users";

    /**
     * 设备管理接口路径
     */
    public static final String DEVICES = API_V0 + "/devices";

    /**
     * 数据分析接口路径
     */
    public static final String ANALYSIS = API_V0 + "/analysis";

    /**
     * 能源分析接口路径
     */
    public static final String ENERGY_ANALYSIS = API_V0 + "/energy/analysis";

    /**
     * 能源查询接口路径
     */
    public static final String ENERGY_QUERY = API_V0 + "/energy/query";

    /**
     * 能源信息接口路径
     */
    public static final String ENERGY_INFO = API_V0 + "/energy/info";

    /**
     * 演示接口路径
     */
    public static final String DEMO = API_V0 + "/demo";

    /**
     * 系统管理接口路径
     */
    public static final String SYSTEM = API_V0 + "/system";

    // ==================== 页面路径 ====================

    /**
     * 区域管理页面路径
     */
    public static final String REGION_MANAGEMENT_PAGE = "/region-management";

    /**
     * 私有构造函数，防止实例化
     */
    private ApiPaths() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}