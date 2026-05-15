#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
MEMBER_ID="${MEMBER_ID:-$MEMBER_USERNAME}"
OUT="$RESP_DIR/PR-07.json"
echo "PR-07: удаление участника"
curl_auth -i -X DELETE "$PR_PROJECTS/$PROJECT_ID/members/$MEMBER_ID" | tee "$OUT"

