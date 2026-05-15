#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

OUT="$RESP_DIR/ID-05.json"
echo "ID-05: refresh access token"
curl -sS -b "$COOKIE_DIR/owner.cookie" -c "$COOKIE_DIR/owner.cookie" -X POST "$ID_REFRESH" \
  | tee "$OUT"
NEW_ACCESS_TOKEN="$(cat "$OUT" | extract_access || true)"
[ -n "$NEW_ACCESS_TOKEN" ] && save_var ACCESS_TOKEN "$NEW_ACCESS_TOKEN"

