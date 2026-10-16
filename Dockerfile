# Build stage — uses JDK 24; the project compiles to a Java 21 target.
FROM eclipse-temurin:24-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline
COPY src/ src/
RUN ./mvnw -q -DskipTests package

# Run stage — slim JRE 21.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/image-fetch-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
