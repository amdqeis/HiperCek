#!/usr/bin/env bash

set -Eeuo pipefail

usage() {
  cat <<'EOF'
Usage:
  ./deploy/deploy_ml_to_vps.sh --host 1.2.3.4 --user ubuntu --domain api.example.com --email admin@example.com [options]

Options:
  --host HOST             IP atau hostname VPS.
  --user USER             User SSH di VPS.
  --domain DOMAIN         Domain yang sudah diarahkan ke VPS.
  --email EMAIL           Email untuk sertifikat SSL Let's Encrypt.
  --remote-dir DIR        Folder tujuan di VPS. Default: /home/USER/apps/hiperCek
  --port PORT             Port lokal FastAPI di VPS. Default: 8000
  --app-name NAME         Nama app PM2. Default: hipercek-ml-api
  --skip-ssl              Lewati proses SSL certbot.
  --skip-packages         Lewati instalasi apt dan npm global.
  --help                  Tampilkan bantuan ini.
EOF
}

HOST=""
SSH_USER=""
DOMAIN=""
EMAIL=""
REMOTE_DIR=""
PORT="8000"
APP_NAME="hipercek-ml-api"
SKIP_SSL="0"
SKIP_PACKAGES="0"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --host)
      HOST="${2:-}"
      shift 2
      ;;
    --user)
      SSH_USER="${2:-}"
      shift 2
      ;;
    --domain)
      DOMAIN="${2:-}"
      shift 2
      ;;
    --email)
      EMAIL="${2:-}"
      shift 2
      ;;
    --remote-dir)
      REMOTE_DIR="${2:-}"
      shift 2
      ;;
    --port)
      PORT="${2:-}"
      shift 2
      ;;
    --app-name)
      APP_NAME="${2:-}"
      shift 2
      ;;
    --skip-ssl)
      SKIP_SSL="1"
      shift
      ;;
    --skip-packages)
      SKIP_PACKAGES="1"
      shift
      ;;
    --help|-h)
      usage
      exit 0
      ;;
    *)
      echo "Argumen tidak dikenal: $1" >&2
      usage
      exit 1
      ;;
  esac
done

if [[ -z "$HOST" || -z "$SSH_USER" || -z "$DOMAIN" || -z "$EMAIL" ]]; then
  echo "--host, --user, --domain, dan --email wajib diisi." >&2
  usage
  exit 1
fi

if [[ -z "$REMOTE_DIR" ]]; then
  REMOTE_DIR="/home/${SSH_USER}/apps/hiperCek"
fi

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOCAL_ML_DIR="${ROOT_DIR}/MachineLearning"
REMOTE_ML_DIR="${REMOTE_DIR}/MachineLearning"
REMOTE_SCRIPT="${REMOTE_ML_DIR}/deploy/setup_vps.sh"
SSH_TARGET="${SSH_USER}@${HOST}"

ssh "${SSH_TARGET}" "mkdir -p '${REMOTE_DIR}'"

rsync -az --delete \
  --exclude 'venv' \
  --exclude '__pycache__' \
  --exclude '*.pyc' \
  --exclude '.pytest_cache' \
  "${LOCAL_ML_DIR}/" "${SSH_TARGET}:${REMOTE_ML_DIR}/"

REMOTE_CMD="chmod +x '${REMOTE_SCRIPT}' && sudo '${REMOTE_SCRIPT}' --domain '${DOMAIN}' --email '${EMAIL}' --app-user '${SSH_USER}' --port '${PORT}' --app-name '${APP_NAME}'"

if [[ "$SKIP_SSL" == "1" ]]; then
  REMOTE_CMD="${REMOTE_CMD} --skip-ssl"
fi

if [[ "$SKIP_PACKAGES" == "1" ]]; then
  REMOTE_CMD="${REMOTE_CMD} --skip-packages"
fi

ssh -t "${SSH_TARGET}" "${REMOTE_CMD}"

echo "Deploy MachineLearning selesai ke ${SSH_TARGET}"
echo "Cek API di https://${DOMAIN}/docs"
