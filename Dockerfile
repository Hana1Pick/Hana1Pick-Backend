FROM azul/zulu-openjdk:17
ENV JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto.x86_64
ENV PATH=$JAVA_HOME/bin:$PATH
VOLUME /tmp
ARG JAR_FILE="build/libs/*.jar"
COPY ${JAR_FILE} app.jar
ENV PROFILE dev
ENTRYPOINT ["java", "-jar", "/app.jar"]