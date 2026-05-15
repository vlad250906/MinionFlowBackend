#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$ROOT_DIR/00-env.sh"
source "$ROOT_DIR/lib.sh"

bash "$ROOT_DIR/init/INIT-01.sh"
bash "$ROOT_DIR/project/PR-02.sh"
bash "$ROOT_DIR/artifact/jar/AR-01.sh"
bash "$ROOT_DIR/artifact/input/IN-01.sh"
bash "$ROOT_DIR/artifact/config/CFG-01.sh"
bash "$ROOT_DIR/artifact/task/TASK-01.sh" || true
bash "$ROOT_DIR/artifact/task/TASK-02.sh" || true

echo "Tests finished. Responses: $RESP_DIR"
