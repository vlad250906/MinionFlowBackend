#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID; need JAR_ID; need INPUT_ID; need CONFIG_ID
OUT="$RESP_DIR/TASK-01.json"
echo "TASK-01: создание запуска задачи"
curl_auth_json -X POST "$AR_PROJECT_PREFIX/$PROJECT_ID/tasks" \
  -d "{\"jarArtifactId\":\"$JAR_ID\",\"inputArtifactId\":\"$INPUT_ID\",\"executionConfigId\":\"$CONFIG_ID\"}" \
  | tee "$OUT"
TASK_ID="$(cat "$OUT" | extract_id || true)"
[ -n "$TASK_ID" ] && save_var TASK_ID "$TASK_ID"

