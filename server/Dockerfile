FROM eclipse-temurin:21-jdk-alpine

ENV TZ=Asia/Seoul

# JAR 복사
ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]
