#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ID-01.json"
echo "ID-01: регистрация пользователя"
curl_json -X POST "$ID_REGISTER" \
  -d "{\"email\":{\"value\":\"$OWNER_EMAIL\"},\"username\":{\"value\":\"$OWNER_USERNAME\"},\"password\":{\"value\":\"$OWNER_PASSWORD\"}}" \
  | tee "$OUT"
OWNER_ID="$(cat "$OUT" | extract_id || true)"
[ -n "$OWNER_ID" ] && save_var OWNER_ID "$OWNER_ID"

