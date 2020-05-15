FROM openjdk:14-jdk-alpine
RUN addgroup -S chat && adduser -S chat -G chat
USER chat:chat
ARG JAR_FILE=dependency/backend*.jar
COPY ${JAR_FILE} chatAPI.jar
ENTRYPOINT ["java","-jar","chatAPI.jar"]
