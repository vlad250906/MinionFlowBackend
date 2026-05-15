#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID; need TASK_ID
OUT="$RESP_DIR/TASK-05.json"
echo "TASK-05: получение output-файлов"
curl_auth -X GET "$AR_PROJECT_PREFIX/$PROJECT_ID/tasks/$TASK_ID/outputs" | tee "$OUT"
OUTPUT_ID="$(cat "$OUT" | jq -r '.[0].id // .items[0].id // .outputs[0].id // empty' 2>/dev/null || true)"
if [ -n "$OUTPUT_ID" ]; then
  save_var OUTPUT_ID "$OUTPUT_ID"
  curl_auth -L -X GET "$AR_PROJECT_PREFIX/$PROJECT_ID/outputs/$OUTPUT_ID/content" -o "$RESP_DIR/TASK-05-output.bin"
fi

