# syntax=docker/dockerfile:1.7

# =========================
# 1️⃣ Build Stage
# =========================
FROM gradle:jdk24-graal AS builder

WORKDIR /app

# Gradle 의존성 캐시 최적화 (레이어로 캐시됨)
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon || true

# 소스 복사
COPY src ./src
COPY .env ./.env

# 테스트용 Redis 설치 & 빌드
RUN apt-get update && apt-get install -y --no-install-recommends redis-server && rm -rf /var/lib/apt/lists/*
RUN redis-server --daemonize yes --requirepass lldj123414 && gradle build --no-daemon

# =========================
# 2️⃣ Layer Extract Stage
# =========================
FROM container-registry.oracle.com/graalvm/jdk:24 AS extractor

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# Spring Boot layered jar 추출
RUN java -Djarmode=layertools -jar app.jar extract


# =========================
# 3️⃣ Runtime Stage
# =========================
FROM container-registry.oracle.com/graalvm/jdk:24

WORKDIR /app

# Spring Boot layers
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/application/ ./

# ── t3.micro JVM 최적화 ──
ENTRYPOINT ["java", \
"-XX:+UseJVMCICompiler", \
"-XX:+UseSerialGC", \
"-XX:MaxRAMPercentage=55", \
"-XX:InitialRAMPercentage=25", \
"-XX:+ExitOnOutOfMemoryError", \
"-XX:+AlwaysPreTouch", \
"-Dspring.profiles.active=prod", \
"org.springframework.boot.loader.launch.JarLauncher"]