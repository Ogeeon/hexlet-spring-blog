FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradlew settings.gradle.kts ./
COPY gradle gradle
COPY app/build.gradle.kts app/

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q || true

COPY app/src app/src
RUN ./gradlew :app:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/app/build/libs/*.jar app.jar

EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=production

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT:-8081}"]
