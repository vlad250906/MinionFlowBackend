#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ID-08.json"
echo "ID-08: изменение username, ожидается публикация события user-change"
curl_auth_json -X PATCH "$ID_CHANGE_USERNAME" \
  -d "{\"username\":{\"value\":\"$OWNER_NEW_USERNAME\"}}" \
  | tee "$OUT"
save_var OWNER_USERNAME "$OWNER_NEW_USERNAME"

