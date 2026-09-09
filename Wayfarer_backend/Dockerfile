FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src ./src
COPY src/main/resources/logback-spring.xml /config/logback-spring.xml

ARG BUILD_TIMESTAMP=default
RUN echo "Building at ${BUILD_TIMESTAMP}" && ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache bash wget

RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /var/wayfarer/hike-previews

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /config/logback-spring.xml /config/logback-spring.xml
COPY entrypoint.sh ./entrypoint.sh

RUN chmod +x ./entrypoint.sh && chown -R spring:spring /app

USER spring:spring

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --spider -q http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["./entrypoint.sh"]