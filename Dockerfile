FROM maven:3.8.4-openjdk-17-slim AS build

RUN mkdir -p /workspace

WORKDIR /workspace

COPY pom.xml /workspace

COPY src /workspace/src

RUN mvn -f pom.xml clean package

FROM openjdk:17-slim

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]