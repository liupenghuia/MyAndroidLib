#!/bin/bash

# ==========================================
# Android Library 上传脚本
# 使用方法: ./upload.sh [版本号]
# 示例: ./upload.sh 1.0.5
# 示例: ./upload.sh 1.0.6-SNAPSHOT
# ==========================================

# 1. 检查有没有传入版本号
if [ -z "$1" ]; then
  echo "❌ 错误: 请输入版本号！"
  echo "用法: ./upload.sh <version>"
  echo "例如: ./upload.sh 1.0.4"
  exit 1
fi

VERSION=$1

# 2. 定义你的 GitHub 账号信息
# 为了安全，建议还是去读 local.properties，但在脚本里为了方便演示，
# 这里提供两种方式。推荐方式A (从 local.properties 读取，不写在脚本里)。
# 这里为了确保脚本能跑，我们演示如何通过脚本传参。

# ⚠️ 请填入你的新 Token (不要提交这个脚本到 git，或者使用 local.properties 里的配置)
# 如果你的 local.properties 已经配置好了 gpr.user 和 gpr.key，下面两行可以留空或者注释掉
GITHUB_USER="liupenghuia"
GITHUB_TOKEN=""

echo "========================================"
echo "📦 准备构建并上传: mylibrary"
echo "🔖 目标版本: $VERSION"
echo "========================================"

# 3. 询问确认 (防止手抖)
read -p "确认要上传吗? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "🚫 操作已取消"
    exit 1
fi

# 4. 执行 Gradle 命令
# 这里的关键是 -PtargetVersion=$VERSION，它把脚本里的变量传给了 Gradle
# 如果你想用 local.properties 里的密码，就去掉后面两个 -P 参数
# 如果你想强制用脚本里的密码，就保留下面这样：

CMD="./gradlew :mylibrary:publish -PtargetVersion=$VERSION"

# 如果脚本里填了 Token，就追加到命令里
if [ -n "$GITHUB_TOKEN" ]; then
    CMD="$CMD -PgithubUser=$GITHUB_USER -PgithubToken=$GITHUB_TOKEN"
fi

echo "🚀 正在执行: $CMD"
$CMD

# 5. 检查结果
if [ $? -eq 0 ]; then
    echo ""
    echo "✅✅✅ 上传成功！"
    echo "Maven 引用: implementation 'com.example.myandroid:mylibrary:$VERSION'"
else
    echo ""
    echo "❌❌❌ 上传失败，请检查上面的日志。"
fi
