# 빌드 단계 (JDK)

# JDK 21 환경 시작
FROM eclipse-temurin:21-jdk-alpine AS builder
# 작업 디렉토리 설정
WORKDIR /workspace
# build.gradle을 보고 의존성 다운로드
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon
# 소스 복사 후 JAR 빌드
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# 실행 단계 (JRE)

# JRE 21 환경 시작
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# root 권한 대신 일반 유저로 앱 실행 (보안용)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
# 빌드 단계에서 만든 JAR 가져오기
COPY --from=builder /workspace/build/libs/*.jar app.jar
# 8080 포트 열고 실행
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
# ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]

