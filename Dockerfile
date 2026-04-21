### Aşama 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Bağımlılık önbelleği — sadece pom.xml değişince yeniden indirilir
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Kaynak kod
COPY src src
RUN ./mvnw clean package -DskipTests -q

### Aşama 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Opsiyonel: ONNX model dosyasını buraya koyun
# COPY model.onnx .

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
