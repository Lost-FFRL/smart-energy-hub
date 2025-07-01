# Smart Energy Hub 部署指南

## 📋 概述

本文档提供 Smart Energy Hub 项目的完整部署指南，包括开发环境、测试环境和生产环境的部署方案。

## 🎯 部署架构

### 系统架构图

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Load Balancer │    │   Web Server    │    │    Database     │
│    (Nginx)      │────│  (Spring Boot)  │────│    (MySQL)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                       ┌─────────────────┐
                       │     Cache       │
                       │    (Redis)      │
                       └─────────────────┘
```

### 环境要求

| 组件 | 最低版本 | 推荐版本 | 说明 |
|------|----------|----------|------|
| Java | 17 | 17+ | JDK 运行环境 |
| MySQL | 8.0 | 8.0+ | 主数据库 |
| Redis | 6.0 | 7.0+ | 缓存数据库（可选） |
| Maven | 3.8 | 3.9+ | 构建工具 |
| Docker | 20.10 | 24.0+ | 容器化部署 |
| Nginx | 1.20 | 1.24+ | 反向代理 |

## 🚀 快速部署

### 方式一：Docker Compose（推荐）

1. **克隆项目**
```bash
git clone https://github.com/your-username/smart-energy-hub.git
cd smart-energy-hub
```

2. **配置环境变量**
```bash
cp .env.example .env
# 编辑 .env 文件，设置数据库密码等配置
```

3. **启动服务**
```bash
docker-compose up -d
```

4. **验证部署**
```bash
curl http://localhost:8080/api/simple-test/health
```

### 方式二：传统部署

详见下方各环境部署说明。

## 🔧 开发环境部署

### 1. 环境准备

**安装 Java 17**
```bash
# macOS
brew install openjdk@17

# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel
```

**安装 MySQL**
```bash
# macOS
brew install mysql
brew services start mysql

# Ubuntu/Debian
sudo apt install mysql-server
sudo systemctl start mysql

# CentOS/RHEL
sudo yum install mysql-server
sudo systemctl start mysqld
```

**安装 Maven**
```bash
# macOS
brew install maven

# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven
```

### 2. 数据库配置

**创建数据库**
```sql
CREATE DATABASE smart_energy_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'seh_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON smart_energy_hub.* TO 'seh_user'@'localhost';
FLUSH PRIVILEGES;
```

**导入初始数据**
```bash
mysql -u seh_user -p smart_energy_hub < sql/init.sql
```

### 3. 应用配置

**复制配置文件**
```bash
cp application.properties.template src/main/resources/application.properties
```

**编辑配置文件**
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/smart_energy_hub
spring.datasource.username=seh_user
spring.datasource.password=your_password

# 服务器配置
server.port=8080

# 日志配置
logging.level.com.huayuan.seh=DEBUG
```

### 4. 启动应用

**方式一：Maven 启动**
```bash
mvn spring-boot:run
```

**方式二：JAR 包启动**
```bash
mvn clean package
java -jar target/smart-energy-hub-1.0.0.jar
```

**方式三：IDE 启动**
- 导入项目到 IntelliJ IDEA 或 Eclipse
- 运行 `SmartEnergyHubApplication.java`

## 🧪 测试环境部署

### 1. 服务器准备

**系统要求**
- OS: Ubuntu 20.04+ / CentOS 8+
- CPU: 2 核心
- 内存: 4GB
- 存储: 50GB

**安装依赖**
```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装 Java 17
sudo apt install openjdk-17-jdk -y

# 安装 MySQL
sudo apt install mysql-server -y

# 安装 Nginx
sudo apt install nginx -y
```

### 2. 数据库部署

**MySQL 安全配置**
```bash
sudo mysql_secure_installation
```

**创建数据库和用户**
```sql
CREATE DATABASE smart_energy_hub_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'seh_test'@'%' IDENTIFIED BY 'strong_password_here';
GRANT ALL PRIVILEGES ON smart_energy_hub_test.* TO 'seh_test'@'%';
FLUSH PRIVILEGES;
```

### 3. 应用部署

**创建应用目录**
```bash
sudo mkdir -p /opt/smart-energy-hub
sudo chown $USER:$USER /opt/smart-energy-hub
```

**上传应用文件**
```bash
# 构建应用
mvn clean package -Dmaven.test.skip=true

# 上传到服务器
scp target/smart-energy-hub-1.0.0.jar user@server:/opt/smart-energy-hub/
scp application.properties user@server:/opt/smart-energy-hub/
```

**创建启动脚本**
```bash
cat > /opt/smart-energy-hub/start.sh << 'EOF'
#!/bin/bash
cd /opt/smart-energy-hub
nohup java -jar -Xmx2g -Xms1g smart-energy-hub-1.0.0.jar > app.log 2>&1 &
echo $! > app.pid
EOF

chmod +x /opt/smart-energy-hub/start.sh
```

**创建停止脚本**
```bash
cat > /opt/smart-energy-hub/stop.sh << 'EOF'
#!/bin/bash
cd /opt/smart-energy-hub
if [ -f app.pid ]; then
    kill $(cat app.pid)
    rm app.pid
fi
EOF

chmod +x /opt/smart-energy-hub/stop.sh
```

### 4. Nginx 配置

**创建配置文件**
```bash
sudo tee /etc/nginx/sites-available/smart-energy-hub << 'EOF'
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        proxy_pass http://localhost:8080;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API 接口不缓存
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        add_header Cache-Control "no-cache, no-store, must-revalidate";
    }
}
EOF
```

**启用配置**
```bash
sudo ln -s /etc/nginx/sites-available/smart-energy-hub /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 5. 系统服务配置

**创建 systemd 服务**
```bash
sudo tee /etc/systemd/system/smart-energy-hub.service << 'EOF'
[Unit]
Description=Smart Energy Hub Application
After=network.target mysql.service

[Service]
Type=forking
User=ubuntu
WorkingDirectory=/opt/smart-energy-hub
ExecStart=/opt/smart-energy-hub/start.sh
ExecStop=/opt/smart-energy-hub/stop.sh
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
```

**启用服务**
```bash
sudo systemctl daemon-reload
sudo systemctl enable smart-energy-hub
sudo systemctl start smart-energy-hub
```

## 🏭 生产环境部署

### 1. 高可用架构

**负载均衡配置**
```nginx
upstream smart_energy_hub {
    server 10.0.1.10:8080 weight=1 max_fails=3 fail_timeout=30s;
    server 10.0.1.11:8080 weight=1 max_fails=3 fail_timeout=30s;
    server 10.0.1.12:8080 weight=1 max_fails=3 fail_timeout=30s;
}

server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://smart_energy_hub;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 健康检查
        proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;
    }
}
```

### 2. 数据库集群

**MySQL 主从配置**

主库配置 (`/etc/mysql/mysql.conf.d/mysqld.cnf`):
```ini
[mysqld]
server-id = 1
log-bin = mysql-bin
binlog-format = ROW
gtid-mode = ON
enforce-gtid-consistency = ON
```

从库配置:
```ini
[mysqld]
server-id = 2
relay-log = relay-log
read-only = 1
```

### 3. 监控配置

**应用监控**
```yaml
# docker-compose.monitoring.yml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana-data:/var/lib/grafana

volumes:
  grafana-data:
```

### 4. 备份策略

**数据库备份脚本**
```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/mysql"
DB_NAME="smart_energy_hub"
DB_USER="backup_user"
DB_PASS="backup_password"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
mysqldump -u$DB_USER -p$DB_PASS --single-transaction --routines --triggers $DB_NAME > $BACKUP_DIR/seh_backup_$DATE.sql

# 压缩备份文件
gzip $BACKUP_DIR/seh_backup_$DATE.sql

# 删除7天前的备份
find $BACKUP_DIR -name "seh_backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: seh_backup_$DATE.sql.gz"
```

**定时备份**
```bash
# 添加到 crontab
crontab -e

# 每天凌晨2点执行备份
0 2 * * * /opt/scripts/backup.sh >> /var/log/backup.log 2>&1
```

## 🐳 Docker 部署

### 1. Dockerfile

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制应用文件
COPY target/smart-energy-hub-1.0.0.jar app.jar
COPY application.properties application.properties

# 创建非root用户
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/simple-test/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/smart_energy_hub
      - SPRING_DATASOURCE_USERNAME=seh_user
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
    depends_on:
      mysql:
        condition: service_healthy
    restart: unless-stopped
    volumes:
      - app-logs:/app/logs

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - MYSQL_DATABASE=smart_energy_hub
      - MYSQL_USER=seh_user
      - MYSQL_PASSWORD=${DB_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
    depends_on:
      - app
    restart: unless-stopped

volumes:
  mysql-data:
  app-logs:
```

### 3. 环境变量文件

```bash
# .env
DB_PASSWORD=your_secure_password
MYSQL_ROOT_PASSWORD=your_root_password
SPRING_PROFILES_ACTIVE=production
```

## 🔒 安全配置

### 1. SSL/TLS 配置

**获取 SSL 证书**
```bash
# 使用 Let's Encrypt
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

**Nginx SSL 配置**
```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    
    # HSTS
    add_header Strict-Transport-Security "max-age=63072000" always;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

### 2. 防火墙配置

```bash
# UFW 配置
sudo ufw enable
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
```

### 3. 应用安全配置

```properties
# application-production.properties

# 安全配置
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12

# 隐藏服务器信息
server.server-header=

# 安全头
security.headers.frame=DENY
security.headers.content-type=nosniff
security.headers.xss=1; mode=block
```

## 📊 性能优化

### 1. JVM 调优

```bash
# 生产环境 JVM 参数
java -jar \
  -Xms2g -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/opt/logs/heapdump.hprof \
  -Dspring.profiles.active=production \
  smart-energy-hub-1.0.0.jar
```

### 2. 数据库优化

```sql
-- MySQL 配置优化
[mysqld]
innodb_buffer_pool_size = 2G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
query_cache_size = 128M
max_connections = 200
```

### 3. 连接池配置

```properties
# HikariCP 配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

## 🔍 故障排除

### 常见问题

**1. 应用启动失败**
```bash
# 检查日志
tail -f /opt/smart-energy-hub/app.log

# 检查端口占用
sudo netstat -tlnp | grep 8080

# 检查 Java 进程
jps -l
```

**2. 数据库连接失败**
```bash
# 测试数据库连接
mysql -h localhost -u seh_user -p smart_energy_hub

# 检查 MySQL 状态
sudo systemctl status mysql

# 查看 MySQL 错误日志
sudo tail -f /var/log/mysql/error.log
```

**3. 内存不足**
```bash
# 检查内存使用
free -h
top -p $(pgrep java)

# 分析堆转储
jmap -dump:format=b,file=heapdump.hprof $(pgrep java)
```

### 日志分析

**应用日志位置**
- 开发环境: `logs/seh.log`
- 生产环境: `/opt/smart-energy-hub/logs/`
- Docker: `/app/logs/`

**日志级别配置**
```properties
# 生产环境日志配置
logging.level.root=WARN
logging.level.com.huayuan.seh=INFO
logging.level.org.springframework.web=INFO
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

## 📈 监控和告警

### 1. 健康检查

```bash
#!/bin/bash
# health_check.sh

HEALTH_URL="http://localhost:8080/api/simple-test/health"
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_URL)

if [ $RESPONSE -eq 200 ]; then
    echo "$(date): Service is healthy"
    exit 0
else
    echo "$(date): Service is unhealthy (HTTP $RESPONSE)"
    # 发送告警通知
    # curl -X POST "https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK" \
    #   -H 'Content-type: application/json' \
    #   --data '{"text":"Smart Energy Hub service is down!"}'
    exit 1
fi
```

### 2. 系统监控

```bash
# 添加到 crontab，每分钟检查一次
* * * * * /opt/scripts/health_check.sh >> /var/log/health_check.log 2>&1
```

## 🔄 更新和回滚

### 1. 蓝绿部署

```bash
#!/bin/bash
# deploy.sh

NEW_VERSION=$1
CURRENT_PORT=8080
NEW_PORT=8081

# 启动新版本
java -jar -Dserver.port=$NEW_PORT smart-energy-hub-$NEW_VERSION.jar &
NEW_PID=$!

# 等待新版本启动
sleep 30

# 健康检查
if curl -f http://localhost:$NEW_PORT/api/simple-test/health; then
    echo "New version is healthy, switching traffic..."
    
    # 更新 Nginx 配置
    sed -i "s/:$CURRENT_PORT/:$NEW_PORT/g" /etc/nginx/sites-available/smart-energy-hub
    sudo nginx -s reload
    
    # 停止旧版本
    kill $(cat app.pid)
    echo $NEW_PID > app.pid
    
    echo "Deployment completed successfully"
else
    echo "New version health check failed, rolling back..."
    kill $NEW_PID
    exit 1
fi
```

### 2. 数据库迁移

```bash
# 备份当前数据库
mysqldump -u root -p smart_energy_hub > backup_before_migration.sql

# 执行迁移脚本
mysql -u root -p smart_energy_hub < migration/v1.1.0.sql

# 验证迁移结果
mysql -u root -p smart_energy_hub -e "SELECT version FROM schema_version ORDER BY installed_on DESC LIMIT 1;"
```

---

## 📞 技术支持

- **部署问题**: [GitHub Issues](https://github.com/your-username/smart-energy-hub/issues)
- **技术讨论**: [GitHub Discussions](https://github.com/your-username/smart-energy-hub/discussions)
- **紧急支持**: support@yourdomain.com

---

**注意**: 本部署指南基于通用配置编写，实际部署时请根据具体环境调整相关参数。