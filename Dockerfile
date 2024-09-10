# Build stage
FROM azul/zulu-openjdk:17 AS build
# 빌드 스테이지의 기본 이미지로 Azul의 Zulu OpenJDK 17 버전을 사용

WORKDIR /app
# 작업 디렉토리를 /app으로 설정

COPY build.gradle settings.gradle ./
COPY src ./src
COPY gradle ./gradle
COPY gradlew .
# 필요한 Gradle 파일들과 소스 코드를 복사

RUN chmod +x ./gradlew
# gradlew 파일에 실행 권한 부여

RUN ./gradlew bootJar --no-daemon
# Gradle을 사용하여 Spring Boot 애플리케이션을 JAR 파일로 빌드

# Run stage
FROM azul/zulu-openjdk-alpine:17-jre
# 실행 스테이지의 기본 이미지로 Azul의 Zulu OpenJDK JRE 17 Alpine 버전을 사용 (더 작은 크기)

WORKDIR /app
# 작업 디렉토리를 /app으로 설정

COPY --from=build /app/build/libs/*.jar app.jar
# 빌드 스테이지에서 생성된 JAR 파일을 현재 이미지로 복사

RUN apk add --no-cache tzdata
# Alpine Linux에 timezone 데이터 패키지 설치

ENV TZ=Asia/Seoul
# 컨테이너의 시간대를 한국 시간으로 설정

ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8
# 로케일을 영어(미국)로 설정하지만, 이는 한국어 지원을 위한 기본 설정임

COPY --from=build /app/build/libs/*.jar app.jar
# 빌드 스테이지에서 생성된 JAR 파일을 다시 한 번 복사 (중복된 명령어로 보임, 제거 가능)

EXPOSE 8080
# 컨테이너가 8080 포트를 사용함을 명시

ENTRYPOINT ["java", "-Duser.language=ko", "-Duser.country=KR", "-jar", "app.jar"]
# Java 애플리케이션을 실행하는 명령어, 한국어 설정을 추가하여 실행