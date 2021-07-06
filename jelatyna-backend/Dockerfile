FROM openjdk:8

WORKDIR /opt/app
COPY ./ .
RUN ./mvnw compile
CMD ./mvnw spring-boot:run