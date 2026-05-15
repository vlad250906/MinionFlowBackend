# MinionFlow Backend API

## MinionFlow Identity Service API

API сервиса identity-service: регистрация пользователей, подтверждение email, вход, JWT-сессии, refresh-сценарий, изменение данных аккаунта и восстановление доступа.

| Метод | Endpoint | Группа (контроллер) | Назначение |
|---|---|---|---|
| `POST` | `/identity-service/api/account-activations` | Аккаунт | Подтверждение аккаунта |
| `POST` | `/identity-service/api/accounts` | Аккаунт | Регистрация пользователя |
| `PATCH` | `/identity-service/api/accounts/me` | Аккаунт | Изменение username |
| `GET` | `/identity-service/api/accounts/me` | Аккаунт | Получение текущего аккаунта |
| `PATCH` | `/identity-service/api/accounts/me/passwords` | Аккаунт | Изменение пароля |
| `PUT` | `/identity-service/api/password-resets` | Восстановление пароля | Завершение восстановления пароля |
| `POST` | `/identity-service/api/password-resets` | Восстановление пароля | Начало восстановления пароля |
| `DELETE` | `/identity-service/api/sessions` | Сессии | Завершение всех сессий |
| `POST` | `/identity-service/api/sessions` | Сессии | Вход пользователя |
| `DELETE` | `/identity-service/api/sessions/me` | Сессии | Завершение текущей сессии |
| `POST` | `/identity-service/api/sessions/refresh` | Сессии | Обновление access token |

## MinionFlow Project Service API

API сервиса project-service: управление проектами, участниками проектов и их ролями.

| Метод | Endpoint | Группа (контроллер) | Назначение |
|---|---|---|---|
| `GET` | `/project-service/projects` | Проекты | Получение списка проектов |
| `POST` | `/project-service/projects` | Проекты | Создание проекта |
| `PATCH` | `/project-service/projects/{projectId}` | Проекты | Изменение проекта |
| `GET` | `/project-service/projects/{projectId}` | Проекты | Получение проекта |
| `DELETE` | `/project-service/projects/{projectId}` | Проекты | Удаление проекта |
| `GET` | `/project-service/projects/{projectId}/members` | Участники проектов | Получение участников проекта |
| `POST` | `/project-service/projects/{projectId}/members` | Участники проектов | Добавление участника |
| `DELETE` | `/project-service/projects/{projectId}/members/{userId}` | Участники проектов | Удаление участника |
| `GET` | `/project-service/projects/{projectId}/members/{userId}` | Участники проектов | Получение участника проекта |
| `PATCH` | `/project-service/projects/{projectId}/members/{userId}` | Участники проектов | Изменение роли участника |

## MinionFlow Artifact Service API

API сервиса artifact-service: управление jar-артефактами, входными файлами, execution config, task run, логами и output-файлами.

| Метод | Endpoint | Группа (контроллер) | Назначение |
|---|---|---|---|
| `GET` | `/artifact-service/api/projects/{projectId}/artifacts` | JAR-артефакты | Получение списка jar-артефактов |
| `POST` | `/artifact-service/api/projects/{projectId}/artifacts` | JAR-артефакты | Загрузка jar-артефакта |
| `PATCH` | `/artifact-service/api/projects/{projectId}/artifacts/{artifactId}` | JAR-артефакты | Изменение метаданных jar-артефакта |
| `GET` | `/artifact-service/api/projects/{projectId}/artifacts/{artifactId}` | JAR-артефакты | Получение метаданных jar-артефакта |
| `DELETE` | `/artifact-service/api/projects/{projectId}/artifacts/{artifactId}` | JAR-артефакты | Удаление jar-артефакта |
| `PUT` | `/artifact-service/api/projects/{projectId}/artifacts/{artifactId}/content` | JAR-артефакты | Обновление содержимого jar-артефакта |
| `GET` | `/artifact-service/api/projects/{projectId}/artifacts/{artifactId}/content` | JAR-артефакты | Получение содержимого jar-артефакта |
| `GET` | `/artifact-service/api/projects/{projectId}/executionConfigs` | Конфигурации запуска | Получение списка конфигураций |
| `POST` | `/artifact-service/api/projects/{projectId}/executionConfigs` | Конфигурации запуска | Создание конфигурации запуска |
| `PATCH` | `/artifact-service/api/projects/{projectId}/executionConfigs/{configId}` | Конфигурации запуска | Изменение конфигурации запуска |
| `GET` | `/artifact-service/api/projects/{projectId}/executionConfigs/{configId}` | Конфигурации запуска | Получение конфигурации запуска |
| `DELETE` | `/artifact-service/api/projects/{projectId}/executionConfigs/{configId}` | Конфигурации запуска | Удаление конфигурации запуска |
| `GET` | `/artifact-service/api/projects/{projectId}/inputs` | Входные данные | Получение списка входных данных |
| `POST` | `/artifact-service/api/projects/{projectId}/inputs` | Входные данные | Загрузка входных данных |
| `PATCH` | `/artifact-service/api/projects/{projectId}/inputs/{artifactId}` | Входные данные | Изменение метаданных входных данных |
| `GET` | `/artifact-service/api/projects/{projectId}/inputs/{artifactId}` | Входные данные | Получение метаданных входных данных |
| `DELETE` | `/artifact-service/api/projects/{projectId}/inputs/{artifactId}` | Входные данные | Удаление входных данных |
| `PUT` | `/artifact-service/api/projects/{projectId}/inputs/{artifactId}/content` | Входные данные | Обновление содержимого входных данных |
| `GET` | `/artifact-service/api/projects/{projectId}/inputs/{artifactId}/content` | Входные данные | Получение содержимого входных данных |
| `GET` | `/artifact-service/api/projects/{projectId}/logs/{microtaskId}` | Логи | Получение backlog-логов |
| `GET` | `/artifact-service/api/projects/{projectId}/outputs/{outputId}` | Output-файлы | Получение метаданных output-файла |
| `GET` | `/artifact-service/api/projects/{projectId}/outputs/{outputId}/content` | Output-файлы | Получение содержимого output-файла |
| `GET` | `/artifact-service/api/projects/{projectId}/tasks` | Задачи | Получение списка задач |
| `POST` | `/artifact-service/api/projects/{projectId}/tasks` | Задачи | Создание запуска задачи |
| `GET` | `/artifact-service/api/projects/{projectId}/tasks/{taskId}` | Задачи | Получение задачи |
| `PATCH` | `/artifact-service/api/projects/{projectId}/tasks/{taskId}` | Задачи | Отмена задачи |
| `GET` | `/artifact-service/api/projects/{projectId}/tasks/{taskId}/agents/{agentId}` | Задачи | Получение swarm-агента |
| `GET` | `/artifact-service/api/projects/{projectId}/tasks/{taskId}/microtasks/stateless/{microtaskId}` | Задачи | Получение stateless-микрозадачи |
| `GET` | `/artifact-service/api/projects/{projectId}/tasks/{taskId}/microtasks/swarm/{microtaskId}` | Задачи | Получение swarm-микрозадачи |
| `GET` | `/artifact-service/api/projects/{projectId}/tasks/{taskId}/outputs` | Задачи | Получение output-файлов задачи |
| `GET` | `/artifact-service/api/projects/{projectId}/tasks/{taskId}/stats/stateless` | Задачи | Получение stateless-состояния |
| `GET` | `/artifact-service/api/projects/{projectId}/tasks/{taskId}/stats/swarm` | Задачи | Получение swarm-состояния |