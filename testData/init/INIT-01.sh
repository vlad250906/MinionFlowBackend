#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

echo "1) Register owner/member/outsider"
bash "$ROOT_DIR/identity/ID-01.sh" || true
OWNER_ID="${OWNER_ID:-$(jq -r '.id // .userId // .accountId // empty' "$RESP_DIR/ID-01.json" 2>/dev/null || true)}"
[ -n "${OWNER_ID:-}" ] && save_var OWNER_ID "$OWNER_ID"

OWNER_TOKEN="${VERIFICATION_TOKEN:-$(latest_identity_token "${OWNER_ID:-}" EMAIL_VERIFICATION)}"
if [ -n "${OWNER_TOKEN:-}" ]; then
  save_var VERIFICATION_TOKEN "$OWNER_TOKEN"
  bash "$ROOT_DIR/identity/ID-03.sh" || true
else
  echo "WARNING: verification token not found. Set VERIFICATION_TOKEN manually and run identity/ID-03.sh"
fi

bash "$ROOT_DIR/identity/ID-04.sh"

curl_json -X POST "$ID_REGISTER" -d "{\"email\":{\"value\":\"$MEMBER_EMAIL\"},\"username\":{\"value\":\"$MEMBER_USERNAME\"},\"password\":{\"value\":\"$MEMBER_PASSWORD\"}}" > "$RESP_DIR/INIT-member-register.json" || true
curl_json -X POST "$ID_REGISTER" -d "{\"email\":{\"value\":\"$OUTSIDER_EMAIL\"},\"username\":{\"value\":\"$OUTSIDER_USERNAME\"},\"password\":{\"value\":\"$OUTSIDER_PASSWORD\"}}" > "$RESP_DIR/INIT-outsider-register.json" || true

bash "$ROOT_DIR/project/PR-01.sh"
echo "State saved to $STATE_FILE"
cat "$STATE_FILE"

