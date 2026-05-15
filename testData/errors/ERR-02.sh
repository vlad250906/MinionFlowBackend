#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ERR-02.json"
echo "ERR-02: несуществующий проект, ожидается 404"
curl -sS -i -X GET "$PR_PROJECTS/00000000-0000-0000-0000-000000000000" -H "Authorization: Bearer ${ACCESS_TOKEN:-}" | tee "$OUT"

