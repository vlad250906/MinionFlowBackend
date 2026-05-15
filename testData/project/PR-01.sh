#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/PR-01.json"
echo "PR-01: создание проекта"
curl_auth_json -X POST "$PR_PROJECTS" \
  -d "{\"name\":\"$PROJECT_NAME\",\"description\":\"$PROJECT_DESCRIPTION\"}" \
  | tee "$OUT"
PROJECT_ID="$(cat "$OUT" | extract_id || true)"
[ -n "$PROJECT_ID" ] && save_var PROJECT_ID "$PROJECT_ID"

