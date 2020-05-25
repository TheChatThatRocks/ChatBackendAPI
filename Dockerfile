# Build
FROM openjdk:11-jdk as build

WORKDIR /workspace/app
COPY gradle gradle
COPY src src
COPY build.gradle .
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle .

RUN ls

# Build app
RUN chmod +x /workspace/app/gradlew
RUN /workspace/app/gradlew clean build -x test

# Extranct files
RUN mkdir -p build/dependency && (cd /workspace/app/build/dependency; jar -xf /workspace/app/build/libs/*.jar)

# Execution
FROM openjdk:11-jdk-slim

EXPOSE 8080
EXPOSE 7070

ENV DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Download wait for it
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /bin/wait-for-it.sh
RUN chmod +x /bin/wait-for-it.sh

ENTRYPOINT ["wait-for-it.sh", "rabbitmq-broker:5672", "--","java","-cp","app:app/lib/*","-Dspring.profiles.active=docker", "com.eina.chat.backendapi.BackEndApiApplication"]
