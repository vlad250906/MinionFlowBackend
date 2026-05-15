#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/PR-05.json"
echo "PR-05: добавление участника по username"
curl_auth_json -X POST "$PR_PROJECTS/$PROJECT_ID/members" \
  -d "{\"username\":{\"value\":\"$MEMBER_USERNAME\"},\"role\":\"USER\"}" \
  | tee "$OUT"
MEMBER_ID="$(cat "$OUT" | jq -r '.userId // .id // .member.userId // empty' 2>/dev/null || true)"
[ -n "$MEMBER_ID" ] && save_var MEMBER_ID "$MEMBER_ID"

