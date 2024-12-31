FROM maven:3-amazoncorretto-17 AS build

WORKDIR /rbs-backend

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src ./src

RUN mvn clean install

FROM amazoncorretto:17

WORKDIR /rbs-backend

# Copy the JAR file from the build stage to the new image
COPY --from=build /rbs-backend/target/rbs-backend-1.0.0.jar /rbs-backend/rbs-backend-app.jar

# Expose the port the application will run on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/rbs-backend/rbs-backend-app.jar"]