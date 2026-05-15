#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/ACL-06.json"
echo "ACL-06: OWNER управляет участниками"
curl -sS -i -X GET "$PR_PROJECTS/$PROJECT_ID/members" -H "Authorization: Bearer $ACCESS_TOKEN" | tee "$OUT"

