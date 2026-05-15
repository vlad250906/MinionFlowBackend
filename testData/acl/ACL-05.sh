#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/ACL-05.json"
echo "ACL-05: MAINTAINER выполняет операцию с артефактом/конфигурацией"
curl -sS -i -X POST "$AR_PROJECT_PREFIX/$PROJECT_ID/inputs" -H "Authorization: Bearer ${MEMBER_ACCESS_TOKEN:-$ACCESS_TOKEN}" -F "file=@$DATA_DIR/input.txt" -F "alias=maintainer-input" | tee "$OUT"

