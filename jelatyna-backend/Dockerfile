FROM openjdk:8

WORKDIR /opt/app
COPY ./ .
RUN ./mvnw package
CMD ./mvnw spring-boot:run
