FROM maven:3.9.11-eclipse-temurin-25-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine AS optimizer
WORKDIR /opt
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=optimizer /opt/dependencies/ ./
COPY --from=optimizer /opt/spring-boot-loader/ ./
COPY --from=optimizer /opt/snapshot-dependencies/ ./
COPY --from=optimizer /opt/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]