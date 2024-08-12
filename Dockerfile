FROM openjdk:17 AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -Dmaven.test.skip=true

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/cbrcurrency*.jar cbrcurrency.jar
EXPOSE 8080
CMD ["java", "-jar", "cbrcurrency.jar"]