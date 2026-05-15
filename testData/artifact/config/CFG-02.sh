#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/CFG-02.json"
echo "CFG-02: создание swarm-sync execution config"
curl_auth_json -X POST "$AR_PROJECT_PREFIX/$PROJECT_ID/executionConfigs" \
  --data-binary "@$DATA_DIR/swarm-sync-config.json" \
  | tee "$OUT"
SWARM_CONFIG_ID="$(cat "$OUT" | extract_id || true)"
[ -n "$SWARM_CONFIG_ID" ] && save_var SWARM_CONFIG_ID "$SWARM_CONFIG_ID"

