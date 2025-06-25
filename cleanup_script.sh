#!/bin/bash

# 智能能源中心 - 代码清理脚本
# 用于移除多余的代码文件，保留核心功能

echo "🧹 开始清理多余代码..."

# 创建备份目录
BACKUP_DIR="./backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"
echo "📦 创建备份目录: $BACKUP_DIR"

# 要清理的文件列表
FILES_TO_REMOVE=(
    "src/main/java/com/kfblue/seh/scheduler/DeviceDataScheduler.java"
    "src/main/java/com/kfblue/seh/scheduler/PumpDeviceDataScheduler.java"
    "src/main/java/com/kfblue/seh/scheduler/LightingDeviceStatusScheduler.java"
    "src/main/java/com/kfblue/seh/controller/DeviceDataTestController.java"
    "PUMP_DEVICE_SCHEDULER_README.md"
    "LIGHTING_SCHEDULER_README.md"
)

# 备份并移除文件
for file in "${FILES_TO_REMOVE[@]}"; do
    if [ -f "$file" ]; then
        echo "📋 备份文件: $file"
        cp "$file" "$BACKUP_DIR/"
        echo "🗑️  移除文件: $file"
        rm "$file"
    else
        echo "⚠️  文件不存在: $file"
    fi
done

# 要保留的核心文件
echo ""
echo "✅ 保留的核心文件:"
echo "   - SimpleSchedulerTest.java (简化定时器)"
echo "   - SimpleTestController.java (简化测试控制器)"
echo "   - SIMPLE_TEST_README.md (使用说明)"
echo "   - VERIFICATION_REPORT.md (验证报告)"

# 检查是否有其他可能的多余文件
echo ""
echo "🔍 检查其他可能的多余文件:"

# 查找可能的测试文件
find src -name "*Test*.java" -not -path "*/test/*" | while read -r file; do
    if [[ "$file" != *"SimpleSchedulerTest"* ]] && [[ "$file" != *"SimpleTestController"* ]]; then
        echo "   ⚠️  发现可能的测试文件: $file"
    fi
done

# 查找可能的调度器文件
find src -name "*Scheduler*.java" | while read -r file; do
    if [[ "$file" != *"SimpleSchedulerTest"* ]]; then
        echo "   ⚠️  发现可能的调度器文件: $file"
    fi
done

# 查找可能的README文件
find . -maxdepth 1 -name "*README*.md" | while read -r file; do
    if [[ "$file" != "./README.md" ]] && [[ "$file" != "./SIMPLE_TEST_README.md" ]] && [[ "$file" != "./VERIFICATION_REPORT.md" ]]; then
        echo "   ⚠️  发现可能的说明文件: $file"
    fi
done

echo ""
echo "🎯 清理完成!"
echo "📦 备份文件保存在: $BACKUP_DIR"
echo "🔄 如需恢复，请从备份目录复制文件"

echo ""
echo "📋 下一步建议:"
echo "   1. 重新编译项目: mvn clean package -DskipTests"
echo "   2. 启动应用测试: java -jar target/smart-energy-hub-0.0.1-SNAPSHOT.jar"
echo "   3. 验证功能: curl http://localhost:8080/api/simple-test/health"

echo ""
echo "✨ 清理脚本执行完毕!"