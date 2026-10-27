# Библиотечный каталог - REST API на Spring Boot

Spring Boot приложение для управления библиотечным каталогом с REST API, Swagger UI и интеграционными тестами.

## Требования

- Java 17+ (в проекте используется Java 17)
- Maven 3.8+
- Docker (для тестирования в изолированном окружении)

## Сборка и запуск

### Локальная сборка (через Maven)

```bash
cd /home/atambek/LibraryProject/LibraryProject
mvn clean compile
mvn spring-boot:run
```

### Сборка через Docker

```bash
docker run --rm -v $(pwd):/workspace -w /workspace maven:3-eclipse-temurin-17 mvn clean package
```

### Запуск приложения

```bash
# С помощью Maven
mvn spring-boot:run

# Или запустить JAR файл
java -jar target/*.jar
```

Приложение запустится на http://localhost:8080

## API Эндпоинты

### Книги (/api/books)

| Метод | Путь | Описание | Ответ |
|-------|------|----------|-------|
| GET | /api/books | Список всех книг (q - параметр поиска по названию) | 200, список BookResponse |
| GET | /api/books/{isbn} | Книга по ISBN | 200, BookResponse |
| POST | /api/books | Добавить книгу | 201, BookResponse + заголовок Location |
| DELETE | /api/books/{isbn} | Удалить книгу | 204 |

### Читатели (/api/readers)

| Метод | Путь | Описание | Ответ |
|-------|------|----------|-------|
| GET | /api/readers | Список всех читателей | 200, список ReaderResponse |
| GET | /api/readers/{cardNumber} | Читатель по номеру билета | 200, ReaderResponse |
| POST | /api/readers | Регистрация нового читателя | 201 + заголовок Location |
| GET | /api/readers/{cardNumber}/loans | Активные выдачи читателя | 200, список LoanResponse |

### Выдачи (/api/loans)

| Метод | Путь | Описание | Ответ |
|-------|------|----------|-------|
| GET | /api/loans | Все выдачи (overdue=true - только просроченные) | 200, список LoanResponse |
| GET | /api/loans/{id} | Выдача по номеру | 200, LoanResponse |
| POST | /api/loans | Выдать книгу | 201, LoanResponse + заголовок Location |
| POST | /api/loans/{id}/return | Вернуть книгу | 200, LoanResponse |

### Отчёты (/api/reports)

| Метод | Путь | Описание | Ответ |
|-------|------|----------|-------|
| GET | /api/reports/top?n=5 | Топ-N популярных книг | 200, список BookDto |
| GET | /api/reports/by-category | Количество выдач по категориям | 200, Map<category, count> |
| GET | /api/reports/authors | Все авторы в алфавитном порядке | 200, SortedSet<String> |

## Swagger UI

Документация API доступна по адресу:
- http://localhost:8080/swagger-ui.html
- http://localhost:8080/v3/api-docs

## Конфигурация

Файл `src/main/resources/application.properties`:

```properties
# Порт сервера
server.port=8080

# Путь к файлу импорта (в classpath)
library.import-file=books.csv

# Swagger UI настройки
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

## Формат CSV для импорта книг

Файл `books.csv` должен быть в папке `src/main/resources/`:

```
isbn;title;authors;year;copies
978-5-4461-0914-9;Java. Эффективное программирование;Джошуа Блох;2019;2
978-5-17-090630-2;Война и мир;Л. Н. Толстой;2015;1
```

## Валидация данных

### CreateBookRequest
- `isbn` - обязательное поле, валидный ISBN
- `title` - не может быть пустым
- `authors` - список не может быть пустым
- `year` - минимум 1450
- `copies` - минимум 1

### CreateReaderRequest
- `cardNumber` - формат R-XXXXX (например, R-12345)
- `fullName` - не может быть пустым
- `category` - STUDENT, TEACHER или GUEST

## Обработка ошибок

Все ошибки возвращаются в формате Problem Details (RFC 9457):

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "Читатель R-00003 достиг лимита: 1",
  "instance": "/api/loans"
}
```

Коды ошибок:
- 400 Bad Request - невалидные данные
- 404 Not Found - объект не найден
- 409 Conflict - конфликт (дубликат, лимит, доступность)
- 500 Internal Server Error - серверная ошибка

## Тестирование

```bash
mvn test
```

Запускает 13 тестов:
- `LibraryTest` - 5 тестов утилит LibraryUtils и ReaderCard
- `LibraryLab2Test` - 8 тестов бизнес-логики Library

## Авторы

- Лабораторная работа №2: Базовые классы Library, Book, Reader, Loan
- Лабораторная работа №3: REST API на Spring Boot