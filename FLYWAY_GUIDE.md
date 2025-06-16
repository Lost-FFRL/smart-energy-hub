# Flyway 数据库迁移指南

## 概述

本项目已集成 Flyway 数据库迁移工具，用于管理数据库表结构的版本控制和自动化部署。当项目启动时，Flyway 会自动检查并执行未应用的数据库迁移脚本。

## 配置说明

### 依赖配置

在 `pom.xml` 中已添加以下依赖：

```xml
<!-- Flyway 数据库迁移工具 -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- Flyway MySQL 驱动支持 -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

### 应用配置

在 `application.properties` 中的 Flyway 配置：

```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
spring.flyway.out-of-order=false
spring.flyway.table=flyway_schema_history
```

## 迁移脚本规范

### 文件命名规则

迁移脚本必须遵循以下命名规范：

```
V{版本号}__{描述}.sql
```

示例：
- `V1__Create_demo_table.sql` - 创建 demo 表
- `V2__Insert_demo_data.sql` - 插入初始数据
- `V3__Add_user_table.sql` - 添加用户表
- `V4__Alter_demo_add_column.sql` - 修改 demo 表添加字段

### 版本号规则

- 版本号必须是递增的
- 可以使用数字：`V1`, `V2`, `V3`
- 也可以使用点分版本：`V1.1`, `V1.2`, `V2.0`
- 一旦脚本被执行，不能修改已有的迁移脚本

### 脚本存放位置

所有迁移脚本存放在：
```
src/main/resources/db/migration/
```

## 现有迁移脚本

### V1__Create_demo_table.sql
创建 demo 表，包含以下字段：
- `id` - 主键，自增
- `name` - 名称
- `description` - 描述
- `status` - 状态
- `sort_order` - 排序
- `created_by` - 创建人
- `created_at` - 创建时间
- `updated_by` - 修改人
- `updated_at` - 修改时间
- `deleted` - 逻辑删除标记
- `remark` - 备注信息

### V2__Insert_demo_data.sql
插入 5 条测试数据到 demo 表中。

## 使用指南

### 添加新的迁移脚本

1. 在 `src/main/resources/db/migration/` 目录下创建新的 SQL 文件
2. 按照命名规范命名文件（版本号要比现有的大）
3. 编写 SQL 脚本内容
4. 重启应用程序，Flyway 会自动执行新的迁移脚本

### 示例：添加用户表

创建文件 `V3__Create_user_table.sql`：

```sql
-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

### 最佳实践

1. **向前兼容**：新的迁移脚本应该向前兼容，避免破坏现有数据
2. **原子性**：每个迁移脚本应该是原子性的，要么全部成功，要么全部失败
3. **测试**：在生产环境应用之前，先在开发和测试环境验证迁移脚本
4. **备份**：在执行重要的数据库迁移之前，务必备份数据库
5. **描述性命名**：使用描述性的文件名，便于理解迁移的目的

### 常用 Flyway 命令

```bash
# 查看迁移状态
./mvnw flyway:info

# 验证迁移脚本
./mvnw flyway:validate

# 手动执行迁移（通常不需要，Spring Boot 会自动执行）
./mvnw flyway:migrate

# 清理数据库（谨慎使用，会删除所有数据）
./mvnw flyway:clean
```

## 注意事项

1. **不要修改已执行的迁移脚本**：一旦迁移脚本被执行，就不应该再修改它们
2. **版本控制**：所有迁移脚本都应该纳入版本控制系统
3. **团队协作**：团队成员应该协调版本号，避免冲突
4. **生产环境**：在生产环境部署前，确保所有迁移脚本都经过充分测试

## 故障排除

### 常见问题

1. **迁移失败**：检查 SQL 语法和数据库连接
2. **版本冲突**：确保版本号是唯一且递增的
3. **权限问题**：确保数据库用户有足够的权限执行 DDL 操作

### 日志查看

启动应用时，可以在日志中看到 Flyway 的执行信息：

```
logging.level.org.flywaydb=INFO
```

通过以上配置，您可以在应用启动时看到 Flyway 的详细执行日志。