# Smart Energy Hub 安装指南

## 📋 系统要求

- **Java**: JDK 17 或更高版本
- **Maven**: 3.6+ 
- **MySQL**: 5.7+ 或 8.0+
- **操作系统**: Windows 10+, macOS 10.14+, Linux (Ubuntu 18.04+)

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/your-username/smart-energy-hub.git
cd smart-energy-hub
```

### 2. 数据库准备

#### 创建数据库
```sql
CREATE DATABASE smart_energy_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 创建用户（可选）
```sql
CREATE USER 'seh_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON smart_energy_hub.* TO 'seh_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. 配置应用

#### 方式一：使用配置文件

1. 复制配置模板：
```bash
cp src/main/resources/application.properties.template src/main/resources/application.properties
```

2. 编辑配置文件：
```bash
vim src/main/resources/application.properties
```

3. 修改数据库连接信息：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_energy_hub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&autoReconnect=true
spring.datasource.username=seh_user
spring.datasource.password=your_secure_password
```

#### 方式二：使用环境变量

1. 复制环境变量模板：
```bash
cp .env.example .env
```

2. 编辑环境变量：
```bash
vim .env
```

3. 设置数据库连接：
```bash
DB_URL=jdbc:mysql://localhost:3306/smart_energy_hub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&autoReconnect=true
DB_USERNAME=seh_user
DB_PASSWORD=your_secure_password
```

### 4. 构建和运行

#### 开发环境
```bash
# 安装依赖
./mvnw clean install

# 运行应用
./mvnw spring-boot:run
```

#### 生产环境
```bash
# 构建 JAR 包
./mvnw clean package -DskipTests

# 运行应用
java -jar target/smart-energy-hub-*.jar
```

### 5. 验证安装

应用启动后，访问以下地址验证：

- **主页**: http://localhost:8080
- **API 文档**: http://localhost:8080/swagger-ui.html
- **健康检查**: http://localhost:8080/api/simple-test/health

## 🔧 高级配置

### 环境配置

#### 开发环境
```bash
java -jar app.jar --spring.profiles.active=dev
```

#### 生产环境
```bash
java -jar app.jar --spring.profiles.active=prod
```

### 数据库迁移

项目使用 Flyway 进行数据库版本管理：

```bash
# 查看迁移状态
./mvnw flyway:info

# 执行迁移
./mvnw flyway:migrate

# 清理数据库（谨慎使用）
./mvnw flyway:clean
```

### 日志配置

默认日志文件位置：`logs/seh.log`

自定义日志配置：
```properties
logging.file.path=/custom/log/path
logging.level.com.kfblue.seh=INFO
```

## 🐳 Docker 部署（可选）

### 创建 Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/smart-energy-hub-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 构建和运行

```bash
# 构建镜像
docker build -t smart-energy-hub .

# 运行容器
docker run -d \
  --name seh-app \
  -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/smart_energy_hub" \
  -e DB_USERNAME="seh_user" \
  -e DB_PASSWORD="your_secure_password" \
  smart-energy-hub
```

## 🔍 故障排除

### 常见问题

#### 1. 数据库连接失败
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**解决方案**：
- 检查 MySQL 服务是否启动
- 验证数据库连接信息
- 确认防火墙设置

#### 2. 端口占用
```
Port 8080 was already in use
```

**解决方案**：
- 修改端口：`server.port=8081`
- 或停止占用端口的进程

#### 3. 内存不足
```
java.lang.OutOfMemoryError: Java heap space
```

**解决方案**：
```bash
java -Xmx2g -jar app.jar
```

### 日志分析

查看应用日志：
```bash
tail -f logs/seh.log
```

查看错误日志：
```bash
grep ERROR logs/seh.log
```

## 📞 获取帮助

- **文档**: 查看项目 README.md
- **Issues**: 在 GitHub 上提交问题
- **讨论**: 参与社区讨论

## 🔄 更新应用

```bash
# 拉取最新代码
git pull origin main

# 重新构建
./mvnw clean package -DskipTests

# 重启应用
# 停止当前应用，然后重新启动
```

---

**注意**: 在生产环境中，请确保：
1. 使用强密码
2. 启用 HTTPS
3. 定期备份数据库
4. 监控应用性能