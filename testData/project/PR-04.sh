#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/PR-04.json"
echo "PR-04: удаление проекта. ВНИМАНИЕ: лучше запускать в конце проверок."
curl_auth -i -X DELETE "$PR_PROJECTS/$PROJECT_ID" | tee "$OUT"

