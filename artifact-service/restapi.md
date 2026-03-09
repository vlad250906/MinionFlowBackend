## Для всех:

Authorization: Bearer <accessJWT>

Artifacts (исполняемые файлы)

- POST /api/v1/projects/{projectId}/artifacts - создать артефакт (алиас + файл)
- GET /api/v1/projects/{projectId}/artifacts - список артефактов
- GET /api/v1/projects/{projectId}/artifacts/{artifactId} - получить артефакт (метаданные, автор)
- PATCH /api/v1/projects/{projectId}/artifacts/{artifactId} - обновить алиас, автора
- DELETE /api/v1/projects/{projectId}/artifacts/{artifactId} - удалить артефакт (и все ревизии файла)
- PUT /api/v1/projects/{projectId}/artifacts/{artifactId}/content - обновить файл, старый файл всё ещё остаётся в S3, но все ссылки теперь идут на новую ревизию (+ обновляет авторство)
- GET /api/v1/projects/{projectId}/artifacts/{artifactId}/content - скачать артефакт (файл)


Execution configs (настройки среды исполнения)

- POST /api/v1/projects/{projectId}/execution-configs - создать конфиг исполнения
- GET /api/v1/projects/{projectId}/execution-configs - список конфигов исполнения (ид, алиасы, автор)
- GET /api/v1/projects/{projectId}/execution-configs/{configId} - получить конфиг (детали)
- PATCH /api/v1/projects/{projectId}/execution-configs/{configId} - частично обновить конфиг
- DELETE /api/v1/projects/{projectId}/execution-configs/{configId} - удалить конфиг

Inputs (входные данные)

- POST /api/v1/projects/{projectId}/inputs - создать входные данные (алиас + тип + файл)
- GET /api/v1/projects/{projectId}/inputs - список входных данных (алиасы, авторство, тип, размер)
- GET /api/v1/projects/{projectId}/inputs/{inputId} - получить входные данные (детали/метаданные)
- PATCH /api/v1/projects/{projectId}/inputs/{inputId} - обновить алиас / тип
- DELETE /api/v1/projects/{projectId}/inputs/{inputId} - удалить входные данные
- PUT /api/v1/projects/{projectId}/inputs/{inputId}/content - загрузить/заменить содержимое входных данных (меняет авторство)
- GET /api/v1/projects/{projectId}/inputs/{inputId}/content - скачать содержимое входных данных (меняет авторство)

Tasks (запуски)

- POST /api/v1/projects/{projectId}/tasks - запустить артефакт (создать task/run)
    - (в теле: artifactId, executionConfigId, inputId и мб ещё что-то)
- GET /api/v1/projects/{projectId}/tasks - список запусков (артифакт-ид, артифакт-алиас, входные-ид, входные-алиас, статусы, время выполнения, мб ещё чё=то)
- GET /api/v1/projects/{projectId}/tasks/{taskId} - получить запуск (всё что есть, агрегированное)
- PATCH /api/v1/projects/{projectId}/tasks/{taskId} - отмена запуска
- GET /api/v1/projects/{projectId}/tasks/{taskId}/output/content - получить вывод (файл)
- GET /api/v1/projects/{projectId}/inputs/{inputId}/output - получить вывод (метаданные)

// TODO: всё остальное (выходные, логи и т.п.)