#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/PR-09.json"
echo "PR-09: проверка реплики username после ID-08"
echo "Сценарий: запустить identity/ID-08.sh, подождать доставку RabbitMQ, затем добавить участника по новому username"
curl_auth_json -X POST "$PR_PROJECTS/${PROJECT_ID:-missing-project}/members" \
  -d "{\"username\":{\"value\":\"$OWNER_NEW_USERNAME\"},\"role\":\"USER\"}" \
  | tee "$OUT"

