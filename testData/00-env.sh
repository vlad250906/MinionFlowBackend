#!/usr/bin/env bash

export ROOT_DIR="${ROOT_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)}"
export STATE_FILE="${STATE_FILE:-$ROOT_DIR/.state.env}"
export RESP_DIR="${RESP_DIR:-$ROOT_DIR/responses}"
export COOKIE_DIR="${COOKIE_DIR:-$ROOT_DIR/cookies}"
export DATA_DIR="${DATA_DIR:-$ROOT_DIR/data}"
mkdir -p "$RESP_DIR" "$COOKIE_DIR"

# Адреса сервисов (по дефолту для docker compose, если kubernetes, то нужно исправить)
export IDENTITY_BASE="${IDENTITY_BASE:-http://localhost:8080}"
export PROJECT_BASE="${PROJECT_BASE:-http://localhost:8081}"
export ARTIFACT_BASE="${ARTIFACT_BASE:-http://localhost:8082}"

# Базовые API prefix'ы
export IDENTITY_API="${IDENTITY_API:-$IDENTITY_BASE/identity-service}"
export PROJECT_API="${PROJECT_API:-$PROJECT_BASE/project-service}"
export ARTIFACT_API="${ARTIFACT_API:-$ARTIFACT_BASE/artifact-service/api}"

# identity-service
export ID_REGISTER="${ID_REGISTER:-$IDENTITY_API/account/register}"
export ID_VERIFY="${ID_VERIFY:-$IDENTITY_API/account/verify}"
export ID_LOGIN="${ID_LOGIN:-$IDENTITY_API/session/login}"
export ID_REFRESH="${ID_REFRESH:-$IDENTITY_API/session/refresh}"
export ID_LOGOUT="${ID_LOGOUT:-$IDENTITY_API/session/logout}"
export ID_LOGOUT_ALL="${ID_LOGOUT_ALL:-$IDENTITY_API/session/logout/all}"
export ID_ME="${ID_ME:-$IDENTITY_API/account/me}"
export ID_CHANGE_USERNAME="${ID_CHANGE_USERNAME:-$IDENTITY_API/account/username}"
export ID_CHANGE_PASSWORD="${ID_CHANGE_PASSWORD:-$IDENTITY_API/account/password}"
export ID_RECOVERY_REQUEST="${ID_RECOVERY_REQUEST:-$IDENTITY_API/recovery/request}"
export ID_RECOVERY_CONFIRM="${ID_RECOVERY_CONFIRM:-$IDENTITY_API/recovery/confirm}"

# project-service.
export PR_PROJECTS="${PR_PROJECTS:-$PROJECT_API/projects}"

# artifact-service
export AR_PROJECT_PREFIX="${AR_PROJECT_PREFIX:-$ARTIFACT_API/projects}"

# PostgreSQL
export PGHOST="${PGHOST:-localhost}"
export PGPORT="${PGPORT:-5433}"
export PGUSER="${PGUSER:-postgres}"
export PGPASSWORD="${PGPASSWORD:-postgres}"
export IDENTITY_DB="${IDENTITY_DB:-identity}"

# Тестовые данные
export OWNER_EMAIL="${OWNER_EMAIL:-owner.minionflow.test@example.com}"
export OWNER_USERNAME="${OWNER_USERNAME:-owner_test}"
export OWNER_NEW_USERNAME="${OWNER_NEW_USERNAME:-owner_test_renamed}"
export OWNER_PASSWORD="${OWNER_PASSWORD:-Password123!}"
export OWNER_NEW_PASSWORD="${OWNER_NEW_PASSWORD:-Password12345!}"

export MEMBER_EMAIL="${MEMBER_EMAIL:-member.minionflow.test@example.com}"
export MEMBER_USERNAME="${MEMBER_USERNAME:-member_test}"
export MEMBER_PASSWORD="${MEMBER_PASSWORD:-Password123!}"

export OUTSIDER_EMAIL="${OUTSIDER_EMAIL:-outsider.minionflow.test@example.com}"
export OUTSIDER_USERNAME="${OUTSIDER_USERNAME:-outsider_test}"
export OUTSIDER_PASSWORD="${OUTSIDER_PASSWORD:-Password123!}"

export PROJECT_NAME="${PROJECT_NAME:-minionflow-curl-test-project}"
export PROJECT_DESCRIPTION="${PROJECT_DESCRIPTION:-Project for MinionFlow curl acceptance checks}"
export PROJECT_NEW_NAME="${PROJECT_NEW_NAME:-minionflow-curl-test-project-updated}"

[ -f "$STATE_FILE" ] && source "$STATE_FILE"
