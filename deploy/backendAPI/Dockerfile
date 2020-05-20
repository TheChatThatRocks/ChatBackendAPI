FROM openjdk:14-jdk-alpine
RUN addgroup -S chat && adduser -S chat -G chat
USER chat:chat
# Load complete jar
#ARG JAR_FILE=dependency/backend*.jar
#COPY ${JAR_FILE} chatAPI.jar
#ENTRYPOINT ["java","-jar","chatAPI.jar"]

EXPOSE 8080:8080

# Load data by layer, samll changes are deployed faster
ARG DEPENDENCY=dependency
COPY ${DEPENDENCY}/BOOT-INF/lib app/lib
COPY ${DEPENDENCY}/META-INF app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes app/classes
ENTRYPOINT ["java","-cp", "app:app/lib/*:app/classes/", "-Dspring.profiles.active=localDocker", "com.eina.chat.backendapi.BackEndApiApplication"]
