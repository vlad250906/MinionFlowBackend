#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/AR-01.json"
echo "AR-01: загрузка jar-артефакта"
curl -sS -X POST "$AR_PROJECT_PREFIX/$PROJECT_ID/jars" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F "file=@$DATA_DIR/test-task.jar;type=application/java-archive" \
  -F "alias=test-task" \
  | tee "$OUT"
JAR_ID="$(cat "$OUT" | extract_id || true)"
[ -n "$JAR_ID" ] && save_var JAR_ID "$JAR_ID"

