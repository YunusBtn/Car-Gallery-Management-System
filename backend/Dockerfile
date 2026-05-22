# Build aşaması: Maven ile jar dosyasını üretir
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Önce pom.xml kopyalanır; dependency cache daha verimli çalışır
COPY pom.xml .
RUN mvn dependency:go-offline

# Kaynak kodları kopyala ve production jar üret
COPY src ./src
RUN mvn clean package -DskipTests
# Run aşaması: sadece JRE ile uygulamayı çalıştırır
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Render genelde PORT env verir; Spring Boot'un o porttan dinlemesini sağlarız
ENV SERVER_PORT=${PORT:-8080}

# Build aşamasında oluşan jar'ı runtime image içine al
COPY --from=builder /app/target/*.jar app.jar

# Container başladığında Spring Boot uygulamasını çalıştır
ENTRYPOINT ["java", "-jar", "app.jar"]
