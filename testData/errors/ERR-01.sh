#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ERR-01.json"
echo "ERR-01: невалидный JSON, ожидается 400"
curl -sS -i -X POST "$ID_REGISTER" -H 'Content-Type: application/json' -d '{bad-json' | tee "$OUT"

