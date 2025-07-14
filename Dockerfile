# Step 1: Use an image that includes Maven and JDK 21 for building
FROM maven:3.9-eclipse-temurin-21 AS build

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy your pom.xml and the source code
COPY pom.xml .
COPY src ./src

# Step 4: Run mvn package to build the jar
RUN mvn clean package

# Step 5: Create a new image from OpenJDK 21 for runtime (lighter)
FROM openjdk:21-jdk-slim

# Step 6: Set the working directory inside the container
WORKDIR /app

# Step 7: Copy the JAR file from the build stage into the runtime container
COPY --from=build /app/target/haufe-test-0.0.1-SNAPSHOT.jar app.jar

# Step 8: Expose the port that the app will run on
EXPOSE 8080

# Step 9: Set the command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

