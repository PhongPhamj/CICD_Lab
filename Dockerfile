# FROM openjdk:17-jdk-slim

# ENV APP_HOME /usr/src/app
# WORKDIR $APP_HOME
# COPY target/*.jar $APP_HOME/app.jar

# EXPOSE 8070
# ENTRYPOINT exec java -jar app.jar

FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . /app
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8070
ENTRYPOINT exec java -jar app.jar