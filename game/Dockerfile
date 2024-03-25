FROM openjdk:17-jdk-alpine

VOLUME /tmp

ARG JAR_FILE

COPY ${JAR_FILE} app.jar

EXPOSE 8080
CMD ["java","-jar","/app.jar"]
# docker build --build-arg JAR_FILE=build/libs/game-0.0.1-SNAPSHOT.jar -t remmo1/tic-tac-toe-spring:0.0.1-SNAPSHOT .
# docker run -p 8080:8080 remmo1/tic-tac-toe-spring:0.0.1-SNAPSHOT

