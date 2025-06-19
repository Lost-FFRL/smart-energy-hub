# AI开发用Spring Boot项目规范

## 技术栈约束
- **后端**: Java Spring Boot + MyBatis Plus + SA-Token + Lombok + Hutool
- **数据库**: MySQL + 逻辑删除
- **构建**: Maven + JDK 17+

## 数据库强制规范

### 标准表结构（6个基础字段）
每个表必须包含：
```sql
`id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
`created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`updated_by` varchar(64) DEFAULT NULL COMMENT '修改人',
`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
`deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
`remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
PRIMARY KEY (`id`)
```

### 命名规范
- 表名：小写+下划线，如 `user_info`
- 字段名：小写+下划线
- 所有删除操作必须是逻辑删除

## 代码结构约束

### 必须的分层结构
```
controller/ → service/ → mapper/ → entity/
dto/ vo/ config/ exception/
```

### 实体类规范
- 所有Entity继承BaseEntity（包含6个基础字段）
- 使用Lombok注解
- 配置逻辑删除注解

### 接口规范
- Controller统一返回Result<T>格式
- 所有业务接口添加SA-Token权限注解
- 使用标准CRUD方法名：save/update/delete/page/getById

### 异常处理
- 全局异常处理器
- 统一错误响应格式

## MyBatis Plus配置要点
- 自动填充：created_by, created_at, updated_by, updated_at, deleted
- 逻辑删除：deleted字段
- 驼峰命名转换

## 开发约束
1. **安全第一**：所有接口必须权限校验
2. **日志完整**：关键操作记录日志  
3. **参数校验**：使用@Valid注解
4. **分页查询**：大数据量必须分页
5. **事务管理**：涉及多表操作加@Transactional

## 常用的工具类
- Hutool：常用工具类集合
- Lombok：简化代码

## 引入 Bean
采用注解：@RequiredArgsConstructor 方式

## 接口返回统一格式
Result<T>

```
{
    "code": 200,
    "msg": "success",
    "data": {}
}
```

## AI开发指令
生成代码时：
- 自动创建包含6基础字段的表结构
- Entity自动继承BaseEntity
- Controller自动添加权限注解和统一响应
- Service实现标准CRUD操作
- 自动配置MyBatis Plus的逻辑删除和自动填充