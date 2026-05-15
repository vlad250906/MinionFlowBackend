#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/ACL-04.json"
echo "ACL-04: USER пытается изменить проект/jar, ожидается отказ"
curl -sS -i -X PATCH "$PR_PROJECTS/$PROJECT_ID" -H "Authorization: Bearer ${MEMBER_ACCESS_TOKEN:-bad-token}" -H 'Content-Type: application/json' -d '{"name":"user-forbidden-update"}' | tee "$OUT"

