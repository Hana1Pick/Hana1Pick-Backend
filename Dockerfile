FROM azul/zulu-openjdk:17
VOLUME /tmp
ARG JAR_FILE="build/libs/*.jar"
COPY ${JAR_FILE} app.jar
ENV PROFILE dev
ENTRYPOINT ["java", "-jar", "/app.jar"]