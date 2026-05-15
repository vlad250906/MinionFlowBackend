#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ID-02.json"
echo "ID-02: повторная регистрация с тем же email/username, ожидается 409 Conflict"
curl -sS -i -X POST "$ID_REGISTER" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":{\"value\":\"$OWNER_EMAIL\"},\"username\":{\"value\":\"$OWNER_USERNAME\"},\"password\":{\"value\":\"$OWNER_PASSWORD\"}}" \
  | tee "$OUT"

