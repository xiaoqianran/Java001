# ============================================================
# demo029_mall 多阶段 Dockerfile（Phase 11 继承自前一版本 Phase 10 基线）
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 非 root 运行（安全）
RUN addgroup -S mall && adduser -S mall -G mall
USER mall

COPY --from=builder /app/target/mall-0.1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

# 健康检查由 Docker Compose 或 K8s 负责，这里只做基础
ENTRYPOINT ["java", "-jar", "app.jar"]
