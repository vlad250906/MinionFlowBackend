#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID; need JAR_ID; need INPUT_ID; need CONFIG_ID
echo "ERR-06: недоступность Engine проверяется при остановленном Engine/mock Engine"
OUT="$RESP_DIR/ERR-06.json"
curl -sS -i -X POST "$AR_PROJECT_PREFIX/$PROJECT_ID/tasks" -H "Authorization: Bearer $ACCESS_TOKEN" -H 'Content-Type: application/json' -d "{\"jarArtifactId\":\"$JAR_ID\",\"inputArtifactId\":\"$INPUT_ID\",\"executionConfigId\":\"$CONFIG_ID\"}" | tee "$OUT"

