#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

need PROJECT_ID
echo "ERR-04: недоступность S3/MinIO проверяется при остановленном MinIO: docker stop minio"
OUT="$RESP_DIR/ERR-04.json"
curl -sS -i -X POST "$AR_PROJECT_PREFIX/$PROJECT_ID/inputs" -H "Authorization: Bearer $ACCESS_TOKEN" -F "file=@$DATA_DIR/input.txt" -F "alias=s3-error-check" | tee "$OUT"

