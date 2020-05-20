FROM openjdk:14-jdk-alpine
RUN addgroup -S chat && adduser -S chat -G chat
USER chat:chat

EXPOSE 7799:7799

# Load data by layer, samll changes are deployed faster
ARG DEPENDENCY=dependency
COPY ${DEPENDENCY}/BOOT-INF/lib app/lib
COPY ${DEPENDENCY}/META-INF app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes app/classes
ENTRYPOINT ["java","-cp", "app:app/lib/*:app/classes/", "com.eina.encryption.chat.CryptoApplication"]
