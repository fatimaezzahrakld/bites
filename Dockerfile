# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
    for attempt in 1 2 3 4 5; do \
      mvn -B -ntp -Dmaven.wagon.http.retryHandler.count=5 package && break; \
      if [ "$attempt" -eq 5 ]; then exit 1; fi; \
      sleep 10; \
    done
FROM eclipse-temurin:21-jre
RUN useradd -r -u 10001 appuser
WORKDIR /app
COPY --from=build /app/target/bites-1.0.0.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
