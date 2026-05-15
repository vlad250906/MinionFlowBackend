#!/usr/bin/env bash
set -euo pipefail

save_var() {
  local name="$1"
  local value="${2:-}"
  touch "$STATE_FILE"
  grep -v "^${name}=" "$STATE_FILE" > "$STATE_FILE.tmp" || true
  mv "$STATE_FILE.tmp" "$STATE_FILE"
  printf '%s=%q\n' "$name" "$value" >> "$STATE_FILE"
  export "$name=$value"
}

need() {
  local name="$1"
  if [ -z "${!name:-}" ]; then
    echo "ERROR: variable $name is empty. Run init/INIT-01.sh or previous test scripts first." >&2
    exit 1
  fi
}

extract_id() {
  jq -r '.id // .userId // .accountId // .projectId // .artifactId // .jarId // .inputId // .configId // .executionConfigId // .taskId // .taskRunId // empty'
}

extract_access() {
  jq -r '.accessToken // .access_token // .token // .jwt // .access // empty'
}

print_response() {
  local f="$1"
  echo "--- response saved to $f"
  cat "$f"
  echo
}

curl_json() {
  curl -sS -H 'Content-Type: application/json' "$@"
}

curl_auth_json() {
  need ACCESS_TOKEN
  curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" -H 'Content-Type: application/json' "$@"
}

curl_auth() {
  need ACCESS_TOKEN
  curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" "$@"
}

latest_identity_token() {
  local user_id="${1:-}"
  local type_filter="${2:-}"
  local sql=""
  if [ -n "$type_filter" ]; then
    sql="select token from verification_tickets where user_id = '$user_id' and type = '$type_filter' order by expires_at desc limit 1;"
  else
    sql="select token from verification_tickets where user_id = '$user_id' order by expires_at desc limit 1;"
  fi
  PGPASSWORD="$PGPASSWORD" psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$IDENTITY_DB" -Atc "$sql" 2>/dev/null || true
}
