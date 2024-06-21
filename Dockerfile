FROM azul/zulu-openjdk:17
ENV JAVA_HOME /usr/lib/jvm/zulu-17-amd64
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV PROFILE dev
ENTRYPOINT ["java", "-jar", "/app.jar"]