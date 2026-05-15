#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/PR-08.json"
echo "PR-08: пользователь без прав пытается изменить проект, ожидается 403/404"
curl -sS -i -X PATCH "$PR_PROJECTS/$PROJECT_ID" \
  -H "Authorization: Bearer ${OUTSIDER_ACCESS_TOKEN:-bad-token}" \
  -H 'Content-Type: application/json' \
  -d '{"name":"forbidden-update"}' \
  | tee "$OUT"

