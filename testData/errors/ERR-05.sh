#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

echo "ERR-05: недоступность RabbitMQ проверяется при остановленном RabbitMQ: docker stop rabbitmq"
OUT="$RESP_DIR/ERR-05.json"
curl -sS -i -X PATCH "$ID_CHANGE_USERNAME" -H "Authorization: Bearer ${ACCESS_TOKEN:-}" -H 'Content-Type: application/json' -d "{\"username\":{\"value\":\"rabbitmq_error_check\"}}" | tee "$OUT"

