FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
COPY Api/pom.xml Api/
COPY Application/pom.xml Application/
COPY Domain/pom.xml Domain/
COPY Persistence/pom.xml Persistence/
COPY Shared/pom.xml Shared/

RUN mvn dependency:go-offline -B

COPY Api/src Api/src
COPY Application/src Application/src
COPY Domain/src Domain/src
COPY Persistence/src Persistence/src
COPY Shared/src Shared/src

RUN mvn clean package -pl Api -am -DskipTests

FROM eclipse-temurin:17.0.18_8-jdk
WORKDIR /app
COPY --from=build /build/Api/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]