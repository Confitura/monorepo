# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -DskipTests package

# Runtime stage — eclipse-temurin JRE is Ubuntu-based and ships bash + sh,
# so Coolify's container terminal works.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/jelatyna-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
