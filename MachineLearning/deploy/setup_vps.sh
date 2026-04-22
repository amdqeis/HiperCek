#!/usr/bin/env bash

set -Eeuo pipefail

usage() {
  cat <<'EOF'
Usage:
  sudo ./deploy/setup_vps.sh --domain api.example.com --email admin@example.com [options]

Options:
  --domain DOMAIN         Domain yang akan diarahkan ke FastAPI.
  --email EMAIL           Email untuk registrasi SSL Let's Encrypt.
  --port PORT             Port lokal FastAPI di VPS. Default: 8000
  --app-name NAME         Nama app PM2. Default: hipercek-ml-api
  --app-user USER         User Linux yang menjalankan PM2. Default: pemanggil sudo
  --skip-ssl              Lewati proses SSL certbot.
  --skip-packages         Lewati instalasi apt dan npm global.
  --help                  Tampilkan bantuan ini.
EOF
}

DOMAIN=""
EMAIL=""
PORT="8000"
APP_NAME="hipercek-ml-api"
APP_USER="${SUDO_USER:-$USER}"
SKIP_SSL="0"
SKIP_PACKAGES="0"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --domain)
      DOMAIN="${2:-}"
      shift 2
      ;;
    --email)
      EMAIL="${2:-}"
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
    --app-user)
      APP_USER="${2:-}"
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

if [[ -z "$DOMAIN" || -z "$EMAIL" ]]; then
  echo "--domain dan --email wajib diisi." >&2
  usage
  exit 1
fi

if [[ $EUID -ne 0 ]]; then
  echo "Script ini perlu dijalankan dengan sudo/root." >&2
  exit 1
fi

if ! id "$APP_USER" >/dev/null 2>&1; then
  echo "User Linux '$APP_USER' tidak ditemukan." >&2
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
TEMPLATE_PATH="${SCRIPT_DIR}/nginx-machinelearning.conf.template"
APP_HOME="$(getent passwd "$APP_USER" | cut -d: -f6)"
NGINX_AVAILABLE="/etc/nginx/sites-available/${DOMAIN}"
NGINX_ENABLED="/etc/nginx/sites-enabled/${DOMAIN}"

run_as_app_user() {
  local command="$1"
  su - "$APP_USER" -s /bin/bash -c "export PATH='/usr/local/bin:/usr/bin:/bin'; export PM2_HOME='${APP_HOME}/.pm2'; export PORT='${PORT}'; export PM2_APP_NAME='${APP_NAME}'; cd '${APP_DIR}'; ${command}"
}

install_packages() {
  apt-get update
  DEBIAN_FRONTEND=noninteractive apt-get install -y \
    python3 \
    python3-venv \
    python3-pip \
    nginx \
    certbot \
    python3-certbot-nginx \
    nodejs \
    npm \
    rsync

  npm install -g pm2
}

install_python_dependencies() {
  python3 -m venv "${APP_DIR}/venv"
  "${APP_DIR}/venv/bin/pip" install --upgrade pip
  "${APP_DIR}/venv/bin/pip" install -r "${APP_DIR}/requirements.txt"
  chown -R "${APP_USER}:${APP_USER}" "${APP_DIR}"
}

configure_pm2() {
  local pm2_bin
  pm2_bin="$(command -v pm2 || true)"

  if [[ -z "$pm2_bin" ]]; then
    echo "PM2 tidak ditemukan. Pastikan instalasi package berjalan sukses." >&2
    exit 1
  fi

  if run_as_app_user "${pm2_bin} describe '${APP_NAME}' >/dev/null 2>&1"; then
    run_as_app_user "${pm2_bin} reload '${APP_NAME}' --update-env"
  else
    run_as_app_user "${pm2_bin} start '${APP_DIR}/ecosystem.config.cjs' --update-env"
  fi

  run_as_app_user "${pm2_bin} save"
  "${pm2_bin}" startup systemd -u "${APP_USER}" --hp "${APP_HOME}" >/dev/null
}

configure_nginx() {
  sed \
    -e "s/__DOMAIN__/${DOMAIN}/g" \
    -e "s/__PORT__/${PORT}/g" \
    "${TEMPLATE_PATH}" > "${NGINX_AVAILABLE}"

  ln -sfn "${NGINX_AVAILABLE}" "${NGINX_ENABLED}"
  nginx -t
  systemctl reload nginx
}

configure_ssl() {
  certbot --nginx \
    --non-interactive \
    --agree-tos \
    --redirect \
    -m "${EMAIL}" \
    -d "${DOMAIN}"
}

if [[ "$SKIP_PACKAGES" != "1" ]]; then
  install_packages
fi

install_python_dependencies
configure_pm2
configure_nginx

if [[ "$SKIP_SSL" != "1" ]]; then
  configure_ssl
fi

systemctl restart nginx

echo "Deploy FastAPI selesai."
echo "PM2 app : ${APP_NAME}"
echo "Domain  : https://${DOMAIN}"
echo "App dir : ${APP_DIR}"
