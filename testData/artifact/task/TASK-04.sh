#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
MICROTASK_ID="${MICROTASK_ID:-00000000-0000-0000-0000-000000000000}"
OUT="$RESP_DIR/TASK-04.json"
echo "TASK-04: получение backlog-логов микрозадачи"
curl_auth -X GET "$AR_PROJECT_PREFIX/$PROJECT_ID/microtasks/$MICROTASK_ID/logs" | tee "$OUT"

