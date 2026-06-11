FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src/ src/
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/library-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
# Запускаем .war как обычный jar через "java -jar" (Spring Boot WarLauncher).
# Это единственный вариант, при котором JSP/Jasper корректно резолвят
# /WEB-INF/views/*.jsp — ручной запуск через "java -cp ... MainClass"
# ломает резолвинг JSP-вьюшек.
ENTRYPOINT ["java", "-jar", "app.war"]
