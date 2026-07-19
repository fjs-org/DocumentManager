# --- STAGE 1: Build ---
# Start with the official JDK 25 image
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Install Maven manually
RUN apt-get update && apt-get install -y maven

RUN ls -lart

# Root POM
COPY pom.xml .

# Build the app
COPY backend/pom.xml ./backend/pom.xml
COPY backend/src ./backend/src

RUN pwd
RUN ls -lart
RUN mvn clean package -DskipTests
RUN ls -lart target/

# --- STAGE 2: Runtime ---
# JRE 25 is available and works perfectly
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

RUN ls -lart

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]