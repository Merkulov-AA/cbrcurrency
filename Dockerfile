FROM maven:3.8.6-jdk-17 AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/target/cbrcurrency*.jar cbrcurrency.jar
EXPOSE 8080
CMD ["java", "-jar", "cbrcurrency.jar"]