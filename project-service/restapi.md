## Для всех:

Authorization: Bearer <accessJWT>

## Projects:
- GET /api/projects - список + пагинация
- POST /api/projects - создать проект
- GET /api/projects/{projectId} - получить проект (название, описание)
- PATCH /api/projects/{projectId} - частично обновить название, описание (OWNER)
	- body типа: { "name": "abcd", "description": "qwerty qwerty \r\n 42" }
- DELETE /api/projects/{projectId} - удалить проект (OWNER)

## Members:
- GET /api/projects/{projectId}/members - список участников с ролями
- POST /api/projects/{projectId}/members - добавить участника (OWNER)
	- body типа: { "userId": "1234", "role": "owner/maintainer/user" }
- GET /api/projects/{projectId}/members/{userId} - права юзера в проекте
- PATCH /api/projects/{projectId}/members/{userId} - поменять роль челика (OWNER)
	- body типа: { "role": "maintainer" }
- DELETE /api/projects/{projectId}/members/{userId} - удалить участника из проекта (OWNER)