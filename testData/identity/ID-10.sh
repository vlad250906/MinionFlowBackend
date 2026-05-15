#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT1="$RESP_DIR/ID-10-request.json"
OUT2="$RESP_DIR/ID-10-confirm.json"
echo "ID-10: восстановление пароля"
curl_json -X POST "$ID_RECOVERY_REQUEST" \
  -d "{\"email\":{\"value\":\"$OWNER_EMAIL\"}}" \
  | tee "$OUT1"
RECOVERY_TOKEN="${RECOVERY_TOKEN:-$(latest_identity_token "${OWNER_ID:-}" ACCOUNT_RECOVERY)}"
if [ -z "${RECOVERY_TOKEN:-}" ]; then
  echo "WARNING: recovery token not found. Set RECOVERY_TOKEN manually and repeat confirm request."
  exit 0
fi
curl -sS -i -X POST "$ID_RECOVERY_CONFIRM" \
  -H 'Content-Type: application/json' \
  -d "{\"accountId\":\"${OWNER_ID:-}\",\"recoveryToken\":\"$RECOVERY_TOKEN\",\"newPassword\":{\"value\":\"$OWNER_NEW_PASSWORD\"}}" \
  | tee "$OUT2"

