#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/ACL-03.json"
echo "ACL-03: неучастник проекта запрашивает данные, ожидается 403/404"
curl -sS -i -X GET "$PR_PROJECTS/$PROJECT_ID" -H "Authorization: Bearer ${OUTSIDER_ACCESS_TOKEN:-bad-token}" | tee "$OUT"

