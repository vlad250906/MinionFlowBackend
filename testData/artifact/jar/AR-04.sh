#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID; need JAR_ID
OUT="$RESP_DIR/AR-04.json"
echo "AR-04: удаление jar-артефакта. ВНИМАНИЕ: запускать ТОЛЬКО после TASK-проверок."
curl_auth -i -X DELETE "$AR_PROJECT_PREFIX/$PROJECT_ID/jars/$JAR_ID" | tee "$OUT"

