#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ID-06.json"
echo "ID-06: logout текущей сессии"
curl -sS -i -b "$COOKIE_DIR/owner.cookie" -X POST "$ID_LOGOUT" \
  -H "Authorization: Bearer ${ACCESS_TOKEN:-}" \
  | tee "$OUT"

