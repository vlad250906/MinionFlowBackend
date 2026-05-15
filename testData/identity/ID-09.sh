#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ID-09.json"
echo "ID-09: изменение пароля"
curl_auth_json -X PATCH "$ID_CHANGE_PASSWORD" \
  -d "{\"oldPassword\":{\"value\":\"$OWNER_PASSWORD\"},\"newPassword\":{\"value\":\"$OWNER_NEW_PASSWORD\"}}" \
  | tee "$OUT"
save_var OWNER_PASSWORD "$OWNER_NEW_PASSWORD"

