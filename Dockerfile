FROM gradle:6.8.2-jdk15 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:15.0.2-slim-buster

RUN addgroup todo && useradd -g todo todo
USER todo:todo

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=local", "-Xmx512m", "-Xms256m"]