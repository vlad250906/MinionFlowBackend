#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ID-04.json"
echo "ID-04: вход пользователя"
curl -sS -c "$COOKIE_DIR/owner.cookie" -X POST "$ID_LOGIN" \
  -H 'Content-Type: application/json' \
  -d "{\"login\":\"$OWNER_EMAIL\",\"password\":{\"value\":\"$OWNER_PASSWORD\"}}" \
  | tee "$OUT"
ACCESS_TOKEN="$(cat "$OUT" | extract_access || true)"
[ -n "$ACCESS_TOKEN" ] && save_var ACCESS_TOKEN "$ACCESS_TOKEN"

