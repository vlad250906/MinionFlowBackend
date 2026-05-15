#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ACL-01.json"
echo "ACL-01: защищённый запрос без JWT, ожидается 401"
curl -sS -i -X GET "$ID_ME" | tee "$OUT"

