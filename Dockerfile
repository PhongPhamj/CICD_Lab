# FROM openjdk:17-jdk-slim

# ENV APP_HOME /usr/src/app
# WORKDIR $APP_HOME
# COPY target/*.jar $APP_HOME/app.jar

# EXPOSE 8070
# ENTRYPOINT exec java -jar app.jar

FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean
RUN mvn clean package -DskipTests

# FROM openjdk:17-jdk-slim
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT exec java -jar app.jar