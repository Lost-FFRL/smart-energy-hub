package com.kfblue.seh.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.dto.LoginDTO;
import com.kfblue.seh.entity.SysUser;
import com.kfblue.seh.mapper.SysUserMapper;
import com.kfblue.seh.service.SysUserService;
import com.kfblue.seh.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 系统用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final SysUserMapper sysUserMapper;
    
    @Override
    public LoginVO login(LoginDTO loginDTO, String clientIp) {
        // 1. 参数校验
        if (StrUtil.isBlank(loginDTO.getUsername()) || StrUtil.isBlank(loginDTO.getPassword())) {
            throw new RuntimeException("用户名或密码不能为空");
        }
        
        // 2. 查询用户
        SysUser user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 3. 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 4. 验证密码
        if (!checkPassword(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 5. 执行登录
        StpUtil.login(user.getId());
        
        // 6. 更新最后登录信息
        updateLastLoginInfo(user.getId(), clientIp);
        
        // 7. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(StpUtil.getTokenValue());
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(StpUtil.getTokenTimeout());
        
        // 设置用户信息
        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setGender(user.getGender());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setDeptId(user.getDeptId());
        userInfo.setRoleId(user.getRoleId());
        userInfo.setLastLoginTime(user.getLastLoginTime());
        
        loginVO.setUserInfo(userInfo);
        
        log.info("用户登录成功: {}, IP: {}", user.getUsername(), clientIp);
        return loginVO;
    }
    
    @Override
    public boolean logout() {
        try {
            StpUtil.logout();
            log.info("用户登出成功");
            return true;
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return false;
        }
    }
    
    @Override
    public SysUser getCurrentUser() {
        try {
            // 检查是否已登录
            StpUtil.checkLogin();
            
            // 获取当前登录用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            
            // 查询用户信息
            SysUser user = getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            return user;
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }
    
    @Override
    public void updateLastLoginInfo(Long userId, String loginIp) {
        sysUserMapper.updateLastLoginInfo(userId, LocalDateTime.now(), loginIp);
    }
    
    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}