FROM gradle:8.5-jdk21 AS builder
WORKDIR /home/gradle/project

COPY build.gradle settings.gradle gradle/ ./
RUN gradle dependencies --no-daemon

COPY src src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y --no-install-recommends postgresql-client \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Copy New Relic agent and config
COPY --chown=spring:spring newrelic/newrelic.jar /newrelic/newrelic.jar
COPY --chown=spring:spring newrelic/newrelic.yml /newrelic/newrelic.yml

USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
