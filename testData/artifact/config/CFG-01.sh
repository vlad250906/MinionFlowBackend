#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/CFG-01.json"
echo "CFG-01: создание stateless execution config"
curl_auth_json -X POST "$AR_PROJECT_PREFIX/$PROJECT_ID/executionConfigs" \
  --data-binary "@$DATA_DIR/stateless-config.json" \
  | tee "$OUT"
CONFIG_ID="$(cat "$OUT" | extract_id || true)"
[ -n "$CONFIG_ID" ] && save_var CONFIG_ID "$CONFIG_ID"

