#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID; need JAR_ID
OUT="$RESP_DIR/AR-03.json"
echo "AR-03: изменение metadata jar-артефакта"
curl_auth_json -X PATCH "$AR_PROJECT_PREFIX/$PROJECT_ID/jars/$JAR_ID" \
  -d '{"alias":"test-task-updated"}' \
  | tee "$OUT"

