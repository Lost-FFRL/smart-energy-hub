package com.kfblue.seh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kfblue.seh.dto.LoginDTO;
import com.kfblue.seh.entity.SysUser;
import com.kfblue.seh.vo.LoginVO;

/**
 * 系统用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @param clientIp 客户端IP
     * @return 登录结果
     */
    LoginVO login(LoginDTO loginDTO, String clientIp);
    
    /**
     * 用户登出
     * @return 是否成功
     */
    boolean logout();
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);
    
    /**
     * 更新最后登录信息
     * @param userId 用户ID
     * @param loginIp 登录IP
     */
    void updateLastLoginInfo(Long userId, String loginIp);
    
    /**
     * 检查密码是否正确
     * @param rawPassword 原始密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    boolean checkPassword(String rawPassword, String encodedPassword);
}