# 1. Базовий образ з JDK 17 і Maven (щоб збирати проєкт)
FROM maven:3.9.0-eclipse-temurin-17 AS build

# 2. Робоча директорія у контейнері
WORKDIR /app

# 3. Копіюємо файли з проєкту
COPY . .

# 4. Запускаємо збірку проєкту, пропускаючи тести для швидкості
RUN mvn clean package -DskipTests

# 5. Другий етап - створення легкого образу з JRE для запуску
FROM eclipse-temurin:17-jre-alpine

# 6. Робоча папка для запуску
WORKDIR /app

# 7. Копіюємо з білд-стадії jar файл у новий образ
COPY --from=build /app/target/*.jar app.jar

# 8. Відкриваємо порт, на якому запускається додаток (зазвичай 8080)
EXPOSE 8080

# 9. Команда для запуску додатку
ENTRYPOINT ["java", "-jar", "app.jar"]