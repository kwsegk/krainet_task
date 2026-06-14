# Krainet Task 

Система из двух микросервисов: аутентификация пользователей и отправка email уведомлений.

---

## Реализованные требования

| # | Требование | 
|---|-----------|
| 1 | CRUD RESTful операции через Spring Boot |
| 2 | Общение между микросервисами (REST + @Async) | 
| 3 | Логирование | 
| 4 | Миграции структуры данных (Liquibase) | 
| 5 | Авторизация через Spring Security и JWT | 
| 6 | Разделение на роли (USER, ADMIN) | 
| 7 | Сборка через Maven |
| 8 | Docker + Docker Compose | 
| 9 | README с инструкцией запуска | 
| 10 | Тестовые данные в БД при старте | 
| 11 | Чистый код, SOLID принципы | 

### Обоснование выбора REST вместо Kafka

Для общения между сервисами выбран REST + `@Async`:
- Сервисы тесно связаны, синхронное общение достаточно
- `@Async` обеспечивает асинхронность — клиент не ждёт отправки письма
- Не требует отдельного брокера сообщений в инфраструктуре
- Kafka была бы оправдана при большом объёме событий или необходимости гарантированной доставки при падении сервиса

---

## Технологический стек

| Сервис | Технологии |
|--------|-----------|
| auth-service | Java 21, Spring Boot, Spring Security, JWT, Hibernate, PostgreSQL, Liquibase |
| notification-service | Java 21, Spring Boot, Spring Mail, Gmail SMTP |
| Инфраструктура | Docker, Docker Compose |

---

## Требования

- Docker
- Docker Compose
- Git

---

## Быстрый старт

### 1. Клонировать репозиторий

```bash
git clone https://github.com/kwsegk/krainet_task.git
cd krainet_task
```

### 2. Создать `.env` файл

```bash
cp .env.example .env
```

Заполнить `.env` своими значениями:

```
DB_USERNAME=postgres
DB_PASSWORD=postgres

JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=2592000000

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your@gmail.com
MAIL_PASSWORD=your-app-password
```

### 3. Настройка Gmail App Password

Для отправки писем необходим App Password от Google:

1. Перейти в [Google аккаунт](https://myaccount.google.com)
2. Безопасность → Двухэтапная аутентификация (включить если не включена)
3. Безопасность → Пароли приложений
4. Создать новый пароль для приложения "Mail"
5. Скопировать сгенерированный пароль в `MAIL_PASSWORD`

> **Важно:** `MAIL_PASSWORD` — это App Password (16 символов), а не обычный пароль от Gmail.

### 4. Запустить систему

```bash
docker-compose up --build
```

Система поднимется автоматически:
- PostgreSQL стартует первым
- auth-service ждёт готовности PostgreSQL
- notification-service стартует независимо

---

## Сервисы

| Сервис | URL |
|--------|-----|
| auth-service | http://localhost:8080 |
| auth-service Swagger UI | http://localhost:8080/swagger-ui/index.html |
| notification-service | http://localhost:8081 |

---

## Тестовые данные

При старте Liquibase автоматически создаёт двух пользователей:

| Username | Password | Role | Email |
|----------|----------|------|-------|
| admin | 12345678 | ADMIN | указан в миграции |
| user | 12345678 | USER | указан в миграции |

> **Важно:** Укажите реальный email для ADMIN в файле
> `auth-service/src/main/resources/db/changelog/changeset/insert-test-data.yaml`
> чтобы получать уведомления.

---

## API эндпоинты

### Аутентификация

| Метод | URL | Описание | Доступ |
|-------|-----|----------|--------|
| POST | /api/auth/register | Регистрация | Все |
| POST | /api/auth/login | Логин | Все |
| POST | /api/auth/refresh | Обновить токен | Все |
| POST | /api/auth/logout | Выход | Все |

### Пользователи

| Метод | URL | Описание | Доступ |
|-------|-----|----------|--------|
| GET | /api/users | Все пользователи | ADMIN |
| GET | /api/users/{id} | Пользователь по ID | USER (свой), ADMIN |
| PUT | /api/users/{id} | Обновить пользователя | USER (свой), ADMIN |
| PATCH | /api/users/{id}/password | Сменить пароль | USER (свой), ADMIN |
| DELETE | /api/users/{id} | Удалить пользователя | USER (свой), ADMIN |

---

## Email уведомления

При действиях пользователя с ролью USER все администраторы получают письма:

| Событие | Тема письма |
|---------|-------------|
| Регистрация | Создан пользователь {username} |
| Обновление данных | Изменён пользователь {username} |
| Удаление аккаунта | Удалён пользователь {username} |
| Смена пароля | Изменён пароль пользователя {username} |

---

## Работа с API через Swagger

1. Открыть http://localhost:8080/swagger-ui/index.html
2. Выполнить `POST /api/auth/login` с данными admin/12345678
3. Скопировать `accessToken` из ответа
4. Нажать кнопку **Authorize 🔒** в правом верхнем углу
5. Вставить токен → нажать Authorize
6. Все запросы теперь идут с токеном автоматически

---

## Управление контейнерами

```bash
# Запустить
docker-compose up --build

# Запустить в фоне
docker-compose up --build -d

# Остановить
docker-compose down

# Остановить и удалить данные БД
docker-compose down -v

# Логи конкретного сервиса
docker-compose logs auth-service
docker-compose logs notification-service
```

---

## Архитектура

```
Клиент
  │
  ▼
auth-service (8080)
  ├── Spring Security + JWT
  ├── PostgreSQL (данные пользователей)
  ├── Liquibase (миграции)
  └── @Async RestTemplate
        │
        ▼
  notification-service (8081)
        └── JavaMailSender → Gmail SMTP → Email
```

### Взаимодействие между сервисами

Выбран REST + `@Async` вместо Kafka:
- Сервисы тесно связаны и взаимодействуют синхронно
- `@Async` обеспечивает асинхронность без сложности брокера сообщений
- Клиент получает ответ немедленно, письмо отправляется в фоновом потоке

---

## Структура проекта

```
krainet-task/
├── auth-service/
│   ├── src/
│   └── Dockerfile
├── notification-service/
│   ├── src/
│   └── Dockerfile
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```