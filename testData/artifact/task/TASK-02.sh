#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID; need TASK_ID
OUT="$RESP_DIR/TASK-02.json"
echo "TASK-02: получение состояния task run"
curl_auth -X GET "$AR_PROJECT_PREFIX/$PROJECT_ID/tasks/$TASK_ID" | tee "$OUT"

