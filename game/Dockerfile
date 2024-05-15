FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle build files
COPY build.gradle .
COPY settings.gradle .

# Copy the Gradle wrapper
COPY gradlew .
COPY gradle ./gradle

# Copy the source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test

# Expose the port the app runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "build/libs/game-0.0.1-SNAPSHOT.jar"]

# docker build --build-arg JAR_FILE=build/libs/game-0.0.1-SNAPSHOT.jar -t remmo1/tic-tac-toe-spring:0.0.1-SNAPSHOT .
# docker run -p 8080:8080 remmo1/tic-tac-toe-spring:0.0.1-SNAPSHOT

