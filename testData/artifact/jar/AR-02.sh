#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
OUT="$RESP_DIR/AR-02.json"
echo "AR-02: получение списка jar-артефактов"
curl_auth -X GET "$AR_PROJECT_PREFIX/$PROJECT_ID/jars" | tee "$OUT"
if [ -n "${JAR_ID:-}" ]; then
  curl_auth -X GET "$AR_PROJECT_PREFIX/$PROJECT_ID/jars/$JAR_ID" | tee "$RESP_DIR/AR-02-metadata.json"
  curl_auth -L -X GET "$AR_PROJECT_PREFIX/$PROJECT_ID/jars/$JAR_ID/content" -o "$RESP_DIR/AR-02-content.jar"
fi

