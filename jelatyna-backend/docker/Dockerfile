is FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests && \
    java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/extracted/dependencies ./
COPY --from=builder /app/target/extracted/spring-boot-loader ./
COPY --from=builder /app/target/extracted/snapshot-dependencies ./
COPY --from=builder /app/target/extracted/application ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
