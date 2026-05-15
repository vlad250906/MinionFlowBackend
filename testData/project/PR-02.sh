#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/PR-02.json"
echo "PR-02: список проектов пользователя"
curl_auth -X GET "$PR_PROJECTS" | tee "$OUT"

