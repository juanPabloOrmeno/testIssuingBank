# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar archivos de proyecto
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn ./.mvn

# Compilar la aplicación
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copiar el JAR compilado desde el stage anterior
COPY --from=builder /app/target/issuingBank-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
