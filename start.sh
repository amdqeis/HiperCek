#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="${ROOT_DIR}/backend/demo"
FRONTEND_DIR="${ROOT_DIR}/frontend"
ML_DIR="${ROOT_DIR}/MachineLearning"

PIDS=()

cleanup() {
  for pid in "${PIDS[@]:-}"; do
    if kill -0 "${pid}" 2>/dev/null; then
      kill "${pid}" 2>/dev/null || true
    fi
  done
}

trap cleanup EXIT INT TERM

require_dir() {
  local dir="$1"

  if [[ ! -d "${dir}" ]]; then
    echo "Directory not found: ${dir}" >&2
    exit 1
  fi
}

start_backend() {
  require_dir "${BACKEND_DIR}"

  (
    cd "${BACKEND_DIR}"
    ./mvnw spring-boot:run
  ) &
  PIDS+=("$!")
}

start_frontend() {
  require_dir "${FRONTEND_DIR}"

  (
    cd "${FRONTEND_DIR}"
    npm run dev
  ) &
  PIDS+=("$!")
}

start_ml() {
  require_dir "${ML_DIR}"

  (
    cd "${ML_DIR}"
    if [[ -x "./venv/bin/uvicorn" ]]; then
      ./venv/bin/uvicorn api:app --reload
    else
      python3 -m uvicorn api:app --reload
    fi
  ) &
  PIDS+=("$!")
}

start_backend
start_frontend
start_ml

echo "Spring Boot backend started from ${BACKEND_DIR}"
echo "Frontend started from ${FRONTEND_DIR}"
echo "Machine Learning API started from ${ML_DIR}"
echo "Press Ctrl+C to stop all services."

wait
