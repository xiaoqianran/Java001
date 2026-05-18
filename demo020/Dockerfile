# ============================================================
# demo019 - Spring Boot 应用 Dockerfile（多阶段构建）
# ============================================================
# 优点：
# 1. 使用 Maven 官方镜像构建（无需本地 Maven）
# 2. 分层构建，减小最终镜像体积
# 3. 只复制必要的文件，提高构建缓存命中率
# ============================================================

# ==================== 构建阶段 ====================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# 先复制 pom.xml，利用 Docker 缓存层
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==================== 运行阶段 ====================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 创建非 root 用户（安全最佳实践）
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 从构建阶段复制 jar
COPY --from=builder /app/target/*.jar app.jar

# 健康检查（可选）
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]