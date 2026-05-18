FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

#non-root user and group with no shell
RUN apk add --no-cache curl && \
    addgroup --system appgroup && \
    adduser --system --ingroup appgroup --no-create-home appuser

COPY --from=build /app/target/*.jar app.jar

#appuser owns the jar file
RUN chown appuser:appgroup app.jar

#switching to non-root
USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]