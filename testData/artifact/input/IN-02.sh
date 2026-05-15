#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID; need INPUT_ID
OUT="$RESP_DIR/IN-02.json"
echo "IN-02: получение metadata и content input-файла"
curl_auth -X GET "$AR_PROJECT_PREFIX/$PROJECT_ID/inputs/$INPUT_ID" | tee "$OUT"
curl_auth -L -X GET "$AR_PROJECT_PREFIX/$PROJECT_ID/inputs/$INPUT_ID/content" -o "$RESP_DIR/IN-02-content.txt"

