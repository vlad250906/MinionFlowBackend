#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need OWNER_ID
VERIFICATION_TOKEN="${VERIFICATION_TOKEN:-$(latest_identity_token "$OWNER_ID" EMAIL_VERIFICATION)}"
if [ -z "${VERIFICATION_TOKEN:-}" ]; then
  echo "ERROR: set VERIFICATION_TOKEN or configure PostgreSQL access in 00-env.sh" >&2
  exit 1
fi
save_var VERIFICATION_TOKEN "$VERIFICATION_TOKEN"
OUT="$RESP_DIR/ID-03.json"
echo "ID-03: подтверждение аккаунта"
curl -sS -i -X POST "$ID_VERIFY" \
  -H 'Content-Type: application/json' \
  -d "{\"accountId\":\"$OWNER_ID\",\"verificationToken\":\"$VERIFICATION_TOKEN\"}" \
  | tee "$OUT"

