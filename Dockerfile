FROM openjdk:17-jdk-slim AS build

RUN apt-get update && apt-get install -y --no-install-recommends \ 
    openjdk-17-jdk \ 
    && rm -rf /var/lib/apt/lists/*

COPY . .

RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon

FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY --from=build /build/libs/brevity-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]