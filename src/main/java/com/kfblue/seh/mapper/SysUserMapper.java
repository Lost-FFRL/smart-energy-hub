package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectByUsername(@Param("username") String username);
    
    /**
     * 更新最后登录信息
     * @param userId 用户ID
     * @param loginTime 登录时间
     * @param loginIp 登录IP
     * @return 更新行数
     */
    int updateLastLoginInfo(@Param("userId") Long userId, 
                           @Param("loginTime") java.time.LocalDateTime loginTime, 
                           @Param("loginIp") String loginIp);
}