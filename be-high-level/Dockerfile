# 1) Build stage: use Maven to create the jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2) Run stage: copy the jar into a smaller JRE image
FROM eclipse-temurin:21-jre
WORKDIR /app
# expose the port Spring Boot uses (you set server.port=4000)
EXPOSE 4000
COPY --from=build /app/target/be-demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]