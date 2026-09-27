# ==========================================
# Estágio 1: Build da aplicação Spring Boot
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copia arquivos do Maven Wrapper e POM para cache eficiente de camadas
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copia o código-fonte e compila gerando o JAR executável
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# Estágio 2: Imagem final de runtime leve
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Criação de usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia o JAR compilado do estágio anterior
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
