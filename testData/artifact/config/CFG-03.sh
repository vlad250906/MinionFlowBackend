#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/CFG-03.json"
echo "CFG-03: невалидная конфигурация, ожидается 400"
curl -sS -i -X POST "$AR_PROJECT_PREFIX/$PROJECT_ID/executionConfigs" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  --data-binary "@$DATA_DIR/invalid-config.json" \
  | tee "$OUT"

