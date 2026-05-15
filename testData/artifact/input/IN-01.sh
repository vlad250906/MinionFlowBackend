#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/IN-01.json"
echo "IN-01: загрузка входных данных"
curl -sS -X POST "$AR_PROJECT_PREFIX/$PROJECT_ID/inputs" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F "file=@$DATA_DIR/input.txt;type=text/plain" \
  -F "alias=test-input" \
  -F "type=TEXT" \
  | tee "$OUT"
INPUT_ID="$(cat "$OUT" | extract_id || true)"
[ -n "$INPUT_ID" ] && save_var INPUT_ID "$INPUT_ID"

