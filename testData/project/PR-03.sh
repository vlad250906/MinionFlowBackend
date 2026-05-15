#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/PR-03.json"
echo "PR-03: изменение проекта"
curl_auth_json -X PATCH "$PR_PROJECTS/$PROJECT_ID" \
  -d "{\"name\":\"$PROJECT_NEW_NAME\",\"description\":\"Updated by curl test\"}" \
  | tee "$OUT"

