# Build
FROM openjdk:11-jdk as build
WORKDIR /workspace/app
COPY gradle gradle
COPY src src
COPY build.gradle .
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle .

# Build app
RUN chmod +x /workspace/app/gradlew
RUN /workspace/app/gradlew clean build -x test

# Extranct files
RUN mkdir -p build/dependency && (cd /workspace/app/build/dependency; jar -xf /workspace/app/build/libs/*.jar)

# Execution
FROM openjdk:11-jdk
EXPOSE 8080
EXPOSE 7070

RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh
RUN chmod +x wait-for-it.sh

ENV DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["./wait-for-it.sh", "rabbitmq-broker:5672", "--","java","-cp","app:app/lib/*","-Dspring.profiles.active=docker", "com.eina.chat.backendapi.BackEndApiApplication"]
