package com.kfblue.seh.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录响应VO
 */
@Data
public class LoginVO {
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 过期时间（秒）
     */
    private Long expiresIn;
    
    /**
     * 用户信息
     */
    private UserInfo userInfo;
    
    /**
     * 用户信息内部类
     */
    @Data
    public static class UserInfo {
        
        /**
         * 用户ID
         */
        private Long id;
        
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 真实姓名
         */
        private String realName;
        
        /**
         * 手机号
         */
        private String phone;
        
        /**
         * 邮箱
         */
        private String email;
        
        /**
         * 性别：0-女，1-男
         */
        private Integer gender;
        
        /**
         * 头像
         */
        private String avatar;
        
        /**
         * 部门ID
         */
        private Long deptId;
        
        /**
         * 角色ID
         */
        private Long roleId;
        
        /**
         * 最后登录时间
         */
        private LocalDateTime lastLoginTime;
    }
}