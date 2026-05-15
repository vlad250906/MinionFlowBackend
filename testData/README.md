# Тестовые данные для MinionFlow Backend (см. ПМИ)

Тут находятся bash скрипты с curl-командами для проверок из ПМИ.

Структура:

- `00-env.sh` - базовые адреса, тестовые пользователи, url пути
- `init/INIT-01.sh` - минимальная инициализация: регистрация, подтверждение, вход, создание проекта
- `identity/ID-*.sh` - проверки identity-service
- `project/PR-*.sh` - проверки project-service
- `artifact/*/*.sh` - проверки artifact-service
- `errors/ERR-*.sh` - проверки ошибок
- `acl/ACL-*.sh` - проверки разграничения доступа
- `data/` - тестовые файлы jar/input/config

Перед запуском проверь адреса и пути в `00-env.sh`. По умолчанию используются порты из docker-compose (так что если запускаешь в kubernetes - придется немного поменять их):

- identity-service: `http://localhost:8080`;
- project-service: `http://localhost:8081`;
- artifact-service: `http://localhost:8082`;
- PostgreSQL: `localhost:5433`.

Пример запуска:

```bash
chmod +x ./**/*.sh *.sh
./init/INIT-01.sh
./identity/ID-07.sh
./project/PR-02.sh
```

Скрипты сохраняют ответы в `responses/` и часть полученных идентификаторов в `.state.env`.