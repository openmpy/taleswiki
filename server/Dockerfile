# Java 21 사용
FROM eclipse-temurin:21-jdk-alpine

# 타임존 설정
ENV TZ=Asia/Seoul

# JAR 복사
ARG JAR_FILE=build/app.jar
COPY ${JAR_FILE} app.jar

# 8080 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
