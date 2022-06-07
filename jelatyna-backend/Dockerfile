FROM openjdk:8

WORKDIR /opt/app
COPY ./ .
RUN ./mvnw clean package
CMD ./mvnw spring-boot:run
